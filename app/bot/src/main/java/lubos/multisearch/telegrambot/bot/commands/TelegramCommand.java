package lubos.multisearch.telegrambot.bot.commands;

import lubos.multisearch.telegrambot.dto.ActionMessage;
import lubos.multisearch.telegrambot.bot.commands.exception.IncorrectInputFormatException;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.abilitybots.api.objects.ReplyCollection;
import org.telegram.telegrambots.abilitybots.api.util.AbilityExtension;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static lubos.multisearch.telegrambot.utils.TelegramHelperUtils.*;
import static java.util.Collections.emptyMap;
import static lombok.AccessLevel.PRIVATE;
import static org.telegram.telegrambots.abilitybots.api.objects.Flag.CALLBACK_QUERY;

@Setter
@Getter
@Accessors(fluent = true, chain = true)
@FieldDefaults(level = PRIVATE)
public class TelegramCommand implements CommandHandler, ParametersExtractor, AbilityExtension {

    RabbitTemplate rabbitTemplate;
    MessageSource messageSource;

    Command command;
    Ability ability;
    ReplyCollection replyCollection;

    CommandHandler commandHandler;
    ParametersExtractor parametersExtractor;

    @Override
    public void handleCommand(MessageContext ctx, Pattern inputPattern, String contextId) {
        try {
            var parameters = parametersExtractor.extractParameters(ctx.update(), inputPattern);
            var actionMessage = new ActionMessage(ctx.user(), command, ctx.chatId(), contextId, parameters);
            rabbitTemplate.convertAndSend(ctx.user().getId().toString(), actionMessage);
        } catch (IncorrectInputFormatException e) {
            var message = SendMessage.builder()
                    .chatId(ctx.chatId())
                    .text(e.getLocalizedMessage(messageSource, userLocale(ctx)))
                    .parseMode(ParseMode.HTML)
                    .build();
            ctx.bot().getSilent().execute(message);
        } catch (Exception e) {
            ctx.bot().getSilent().send(escape(e.getMessage()), ctx.chatId());
        }
    }

    @Override
    public Map<String, String> extractParameters(Update upd, Pattern pattern) {
        Map<String, String> parameters;
        if (pattern != null) {
            String inputText;
            if (CALLBACK_QUERY.test(upd)) {
                inputText = upd.getCallbackQuery().getData();
            } else {
                inputText = upd.getMessage().getText();
            }
            Matcher matcher = pattern.matcher(inputText);
            if (!matcher.matches()) {
                throw new IncorrectInputFormatException(inputText, ability.name());
            }
            parameters = matcher.namedGroups()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> matcher.group(entry.getKey())));
        } else {
            parameters = emptyMap();
        }
        return parameters;
    }

}
