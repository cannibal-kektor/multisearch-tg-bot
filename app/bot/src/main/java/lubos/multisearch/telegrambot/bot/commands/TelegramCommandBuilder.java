package lubos.multisearch.telegrambot.bot.commands;


import lubos.multisearch.telegrambot.logging.LogHelper;
import lubos.multisearch.telegrambot.bot.utils.TelegramHelperUtils;
import lubos.multisearch.telegrambot.conf.BotInfo;
import lombok.experimental.FieldDefaults;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.objects.*;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNullElse;
import static lombok.AccessLevel.PRIVATE;
import static lubos.multisearch.telegrambot.bot.utils.TelegramHelperUtils.BOT_TYPING;
import static lubos.multisearch.telegrambot.bot.utils.TelegramHelperUtils.userLocale;
import static org.telegram.telegrambots.abilitybots.api.objects.Ability.builder;
import static org.telegram.telegrambots.abilitybots.api.objects.Flag.REPLY;
import static org.telegram.telegrambots.abilitybots.api.objects.Flag.TEXT;
import static org.telegram.telegrambots.abilitybots.api.objects.MessageContext.newContext;


@Component
public class TelegramCommandBuilder {

    static Predicate<MessageContext> ALWAYS_REPLY = ctx -> false;
    static Predicate<MessageContext> HAS_INPUT = ctx -> TEXT.test(ctx.update()) && ctx.arguments().length > 0;
    static Predicate<Update> IS_REPLY_TO_BOT;

    final BotInfo botInfo;
    final RabbitTemplate rabbitTemplate;
    final MessageSource messageSource;
    final LogHelper logHelper;

    public TelegramCommandBuilder(BotInfo botInfo, RabbitTemplate rabbitTemplate,
                                  MessageSource messageSource, LogHelper logHelper) {
        this.botInfo = botInfo;
        this.rabbitTemplate = rabbitTemplate;
        this.messageSource = messageSource;
        IS_REPLY_TO_BOT = upd -> upd.getMessage()
                .getReplyToMessage()
                .getFrom()
                .getUserName().equalsIgnoreCase(botInfo.username());
        this.logHelper = logHelper;
    }

    public Builder newCommand() {
        return new Builder(rabbitTemplate, messageSource);
    }

    @FieldDefaults(level = PRIVATE)
    public static class Builder {
        final RabbitTemplate rabbitTemplate;
        final MessageSource messageSource;
        Command command;
        String commandInfo;
        Privacy privacy;
        Pattern inputPattern;
        String specifyStr;
        Predicate<MessageContext> actionPredicate;
        Predicate<Update> replyPredicate;
        Pattern pagingCallbackPattern;
        ParametersExtractor parametersExtractor;
        CommandHandler commandHandler;
        List<Reply> replies = new ArrayList<>();
        boolean withMenuCallback;
        boolean withReply;
        boolean pageable;

        Builder(RabbitTemplate rabbitTemplate, MessageSource messageSource) {
            this.rabbitTemplate = rabbitTemplate;
            this.messageSource = messageSource;
        }

        public Builder command(Command command) {
            this.command = command;
            return this;
        }

        public Builder commandInfo(String commandInfo) {
            this.commandInfo = commandInfo;
            return this;
        }

        public Builder privacy(Privacy privacy) {
            this.privacy = privacy;
            return this;
        }

        public Builder inputPattern(Pattern inputPattern) {
            this.inputPattern = inputPattern;
            return this;
        }

        public Builder withMenuCallback() {
            this.withMenuCallback = true;
            return this;
        }

        public Builder pageable(Pattern pagingCallbackPattern) {
            this.pageable = true;
            this.pagingCallbackPattern = pagingCallbackPattern;
            return this;
        }

        public Builder withReply(String specifyStr, Predicate<MessageContext> actionPredicate, Predicate<Update> replyPredicate) {
            this.withReply = true;
            this.actionPredicate = actionPredicate;
            this.replyPredicate = replyPredicate;
            this.specifyStr = specifyStr;
            return this;
        }

        public Builder commandHandler(CommandHandler commandHandler) {
            this.commandHandler = commandHandler;
            return this;
        }

        public Builder parameterExtractor(ParametersExtractor parametersExtractor) {
            this.parametersExtractor = parametersExtractor;
            return this;
        }

        public Builder addCallbackReply(Pattern replyPattern, String callbackId) {
            replies.add(constructCallbackReply(replyPattern, callbackId));
            return this;
        }

        public TelegramCommand build() {

            TelegramCommand tgCommand = new TelegramCommand();
            String commandName = command.name().toLowerCase();
            commandHandler = requireNonNullElse(commandHandler, tgCommand);
            parametersExtractor = requireNonNullElse(parametersExtractor, tgCommand);

            Consumer<MessageContext> action;
            if (withReply) {
                action = ctx -> {
                    if (actionPredicate.test(ctx)) {
                        commandHandler.handleCommand(ctx, inputPattern, CommandsConstants.MESSAGE);
                    } else {
                        ctx.bot().getSilent().forceReply(message(specifyStr, userLocale(ctx)), ctx.chatId());
                    }
                };
                replies.add(Reply.of((bot, upd) -> {
                    var message = upd.getMessage();
                    var ctx = newContext(upd, message.getFrom(), message.getChatId(), bot);
                    commandHandler.handleCommand(ctx, inputPattern, CommandsConstants.REPLY_MESSAGE);
                }, REPLY, IS_REPLY_TO_BOT, replyPredicate, isReplyToMessage()));
            } else {
                action = ctx -> commandHandler.handleCommand(ctx, inputPattern, CommandsConstants.MESSAGE);
            }

            if (withMenuCallback) {
                Pattern menuCallbackPattern = Pattern.compile("^menu " + commandName + "$");
                if (withReply) {
                    replies.add(Reply.of((bot, upd) ->
                            bot.getSilent().forceReply(message(specifyStr, userLocale(upd)),
                            upd.getCallbackQuery().getMessage().getChatId()), callbackMatchesPattern(menuCallbackPattern)));
                } else {
                    replies.add(constructCallbackReply(menuCallbackPattern, CommandsConstants.MENU_CALLBACK));
                }
            }
            if (pageable) {
                replies.add(constructCallbackReply(pagingCallbackPattern, CommandsConstants.PAGING_CALLBACK));
            }

            Ability ability = builder()
                    .name(commandName)
                    .info(commandInfo)
                    .locality(Locality.USER)
                    .privacy(privacy)
                    .action(BOT_TYPING.andThen(action))
//                .post(menuCommands())
                    .build();

            return tgCommand
                    .command(command)
                    .rabbitTemplate(rabbitTemplate)
                    .messageSource(messageSource)
                    .ability(ability)
                    .replyCollection(new ReplyCollection(replies))
                    .commandHandler(commandHandler)
                    .parametersExtractor(parametersExtractor);
        }

        Reply constructCallbackReply(Pattern callbackPattern, String callbackId) {
            return Reply.of((bot, upd) -> {
                        var callbackQuery = upd.getCallbackQuery();
                        var ctx = newContext(upd, callbackQuery.getFrom(),
                                callbackQuery.getMessage().getChatId(), bot);
                        commandHandler.handleCommand(ctx, callbackPattern, callbackId);
                    },
                    callbackMatchesPattern(callbackPattern)
            );
        }

        Predicate<Update> callbackMatchesPattern(Pattern callbackPattern) {
            return upd -> upd.hasCallbackQuery() && callbackPattern.asMatchPredicate()
                    .test(upd.getCallbackQuery().getData());

        }


        Predicate<Update> isReplyToMessage() {
            return upd -> {
                Message reply = upd.getMessage().getReplyToMessage();
                String message = message(specifyStr, userLocale(upd));
                return reply.hasText()
                        && reply.getText().equals(message);
            };
        }


        String message(String messageCode, Locale locale) {
            return messageSource.getMessage(messageCode, null, locale);
        }


    }

}
