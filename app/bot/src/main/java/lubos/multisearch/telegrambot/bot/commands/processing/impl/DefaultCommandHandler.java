package lubos.multisearch.telegrambot.bot.commands.processing.impl;

import lombok.Setter;
import lombok.experimental.Accessors;
import lubos.multisearch.telegrambot.bot.commands.Command;
import lubos.multisearch.telegrambot.bot.commands.exception.IncorrectInputFormatException;
import lubos.multisearch.telegrambot.bot.commands.processing.CommandHandler;
import lubos.multisearch.telegrambot.bot.commands.processing.ParametersExtractor;
import lubos.multisearch.telegrambot.dto.ActionMessage;
import lubos.multisearch.telegrambot.logging.LogHelper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.regex.Pattern;

import static lubos.multisearch.telegrambot.bot.utils.TelegramHelperUtils.escape;
import static lubos.multisearch.telegrambot.bot.utils.TelegramHelperUtils.userLocale;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Scope(SCOPE_PROTOTYPE)
@Component
@Accessors(chain = true)
public class DefaultCommandHandler implements CommandHandler {

    final RabbitTemplate rabbitTemplate;
    final MessageSource messageSource;
    final LogHelper logHelper;

    @Setter
    public ParametersExtractor parametersExtractor;
    @Setter
    public Command command;

    public DefaultCommandHandler(RabbitTemplate rabbitTemplate, MessageSource messageSource,
                                 LogHelper logHelper, ParametersExtractor parametersExtractor) {
        this.rabbitTemplate = rabbitTemplate;
        this.messageSource = messageSource;
        this.logHelper = logHelper;
        this.parametersExtractor = parametersExtractor;
    }

    @Override
    public void handleCommand(MessageContext ctx, Pattern inputPattern, String contextId) {
        try {
//            logHelper
            var parameters = parametersExtractor.extractParameters(ctx.update(), inputPattern);
            var actionMessage = new ActionMessage(ctx.user(), command, ctx.chatId(), contextId, parameters);
            rabbitTemplate.convertAndSend(ctx.user().getId().toString(), actionMessage);
        } catch (IncorrectInputFormatException e) {
            e.setCommand(command.name().toLowerCase());
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
}

