package lubos.multisearch.telegrambot.bot.commands;


import lubos.multisearch.telegrambot.bot.commands.processing.CommandHandler;
import lubos.multisearch.telegrambot.bot.commands.processing.ParametersExtractor;
import lubos.multisearch.telegrambot.bot.commands.processing.impl.DefaultCommandHandler;
import lubos.multisearch.telegrambot.conf.BotInfo;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.telegrambots.abilitybots.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static lombok.AccessLevel.PRIVATE;
import static lubos.multisearch.telegrambot.bot.utils.TelegramHelperUtils.BOT_TYPING;
import static lubos.multisearch.telegrambot.bot.utils.TelegramHelperUtils.userLocale;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;
import static org.telegram.telegrambots.abilitybots.api.objects.Ability.builder;
import static org.telegram.telegrambots.abilitybots.api.objects.Flag.*;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;


@Scope(SCOPE_PROTOTYPE)
@FieldDefaults(level = PRIVATE)
@Component
public class TelegramCommandBuilder {

    static Predicate<MessageContext> ALWAYS_REPLY = ctx -> false;
    static Predicate<MessageContext> HAS_INPUT = ctx -> TEXT.test(ctx.update()) && ctx.arguments().length > 0;
    static Predicate<Update> IS_REPLY_TO_BOT;

    final MessageSource messageSource;

    public TelegramCommandBuilder(BotInfo botInfo, MessageSource messageSource) {
        this.messageSource = messageSource;
        IS_REPLY_TO_BOT = REPLY.and(upd -> upd.getMessage()
                .getReplyToMessage()
                .getFrom()
                .getUserName().equalsIgnoreCase(botInfo.username()));
    }

    @Autowired
    CommandHandler commandHandler;
    @Autowired
    ParametersExtractor parametersExtractor;

    Command command;
    String commandInfo;
    Privacy privacy;
    Pattern inputPattern;
    String replySpecifyStr;
    Predicate<MessageContext> actionPredicate;
    Predicate<Update> replyPredicate;
    Pattern pagingCallbackPattern;
    final List<Reply> replies = new ArrayList<>();
    boolean withMenuCallback;
    boolean withReply;
    boolean pageable;


    public TelegramCommandBuilder command(Command command) {
        this.command = command;
        return this;
    }

    public TelegramCommandBuilder commandInfo(String commandInfo) {
        this.commandInfo = commandInfo;
        return this;
    }

    public TelegramCommandBuilder privacy(Privacy privacy) {
        this.privacy = privacy;
        return this;
    }

    public TelegramCommandBuilder inputPattern(Pattern inputPattern) {
        this.inputPattern = inputPattern;
        return this;
    }

    public TelegramCommandBuilder withMenuCallback() {
        this.withMenuCallback = true;
        return this;
    }

    public TelegramCommandBuilder pageable(Pattern pagingCallbackPattern) {
        this.pageable = true;
        this.pagingCallbackPattern = pagingCallbackPattern;
        return this;
    }

    public TelegramCommandBuilder withReply(String replySpecifyStr, Predicate<MessageContext> actionPredicate, Predicate<Update> replyPredicate) {
        this.withReply = true;
        this.actionPredicate = actionPredicate;
        this.replyPredicate = replyPredicate;
        this.replySpecifyStr = replySpecifyStr;
        return this;
    }

    public TelegramCommandBuilder commandHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        return this;
    }

    public TelegramCommandBuilder parameterExtractor(ParametersExtractor parametersExtractor) {
        this.parametersExtractor = parametersExtractor;
        return this;
    }

    public TelegramCommandBuilder addNewCallback(Pattern replyPattern, String callbackId) {
        registerCallback(replyPattern, callbackId);
        return this;
    }

    public TelegramCommand build() {

        TelegramCommand tgCommand = new TelegramCommand();
        String commandName = command.name().toLowerCase();

        if (commandHandler instanceof DefaultCommandHandler defaultImpl)
            defaultImpl
                    .setCommand(command)
                    .setParametersExtractor(parametersExtractor);

        Consumer<MessageContext> action;

        if (withReply) {
            action = ctx -> {
                if (actionPredicate.test(ctx))
                    commandHandler.handleCommand(ctx, inputPattern, CommandsConstants.MESSAGE);
                else
                    forceReply(ctx.bot(), ctx.update());
            };
            replies.add(Reply.of((bot, upd) -> {
                var ctx = retrieveContext(upd, bot);
                commandHandler.handleCommand(ctx, inputPattern, CommandsConstants.REPLY_MESSAGE);
            }, IS_REPLY_TO_BOT, replyPredicate, isReplyToMessage()));
        } else {
            action = ctx -> commandHandler.handleCommand(ctx, inputPattern, CommandsConstants.MESSAGE);
        }

        if (withMenuCallback) {
            Pattern menuCallbackPattern = Pattern.compile("^menu " + commandName + "$");
            if (withReply)
                replies.add(Reply.of(this::forceReply, callbackMatchesPattern(menuCallbackPattern)));
            else
                registerCallback(menuCallbackPattern, CommandsConstants.MENU_CALLBACK);
        }

        if (pageable)
            registerCallback(pagingCallbackPattern, CommandsConstants.PAGING_CALLBACK);

        return tgCommand
                .command(command)
                .ability(builder()
                        .name(commandName)
                        .info(commandInfo)
                        .locality(Locality.USER)
                        .privacy(privacy)
                        .action(BOT_TYPING.andThen(action))
//                .post(menuCommands())
                        .build()
                )
                .replyCollection(new ReplyCollection(replies))
                .commandHandler(commandHandler)
                .parametersExtractor(parametersExtractor);
    }


    void registerCallback(Pattern callbackPattern, String callbackId) {
        var reply = Reply.of((bot, upd) -> {
                    var ctx = retrieveContext(upd, bot);
                    commandHandler.handleCommand(ctx, callbackPattern, callbackId);
                },
                callbackMatchesPattern(callbackPattern)
        );
        replies.add(reply);
    }

    Predicate<Update> callbackMatchesPattern(Pattern callbackPattern) {
        return upd -> upd.hasCallbackQuery() && callbackPattern.asMatchPredicate()
                .test(upd.getCallbackQuery().getData());

    }


    Predicate<Update> isReplyToMessage() {
        return upd -> {
            Message reply = upd.getMessage().getReplyToMessage();
            String message = message(replySpecifyStr, userLocale(upd));
            return reply.hasText()
                    && reply.getText().equals(message);
        };
    }

    MessageContext retrieveContext(Update upd, BaseAbilityBot bot) {
        if (MESSAGE.test(upd)) {
            var message = upd.getMessage();
            return MessageContext.newContext(upd, message.getFrom(), message.getChatId(), bot);
        } else {
            var callbackQuery = upd.getCallbackQuery();
            return MessageContext.newContext(upd, callbackQuery.getFrom(),
                    callbackQuery.getMessage().getChatId(), bot);
        }
    }

    void forceReply(BaseAbilityBot bot, Update upd) {
        long chatId = getChatId(upd);
        String message = message(replySpecifyStr, userLocale(upd));
        bot.getSilent().forceReply(message, chatId);
    }


    String message(String messageCode, Locale locale) {
        return messageSource.getMessage(messageCode, null, locale);
    }


}

