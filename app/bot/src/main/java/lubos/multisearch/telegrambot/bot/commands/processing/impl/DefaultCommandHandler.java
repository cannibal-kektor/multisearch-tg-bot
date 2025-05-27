package lubos.multisearch.telegrambot.bot.commands.processing.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lubos.multisearch.telegrambot.bot.commands.Command;
import lubos.multisearch.telegrambot.bot.commands.exception.IncorrectInputFormatException;
import lubos.multisearch.telegrambot.bot.commands.processing.CommandHandler;
import lubos.multisearch.telegrambot.bot.commands.processing.ParametersExtractor;
import lubos.multisearch.telegrambot.dto.CommandActionContext;
import lubos.multisearch.telegrambot.logging.LogHelper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.objects.MessageContext;

import java.util.regex.Pattern;

import static lubos.multisearch.telegrambot.bot.utils.TelegramHelperUtils.*;
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
    @Getter
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
            var upd = ctx.update();
            var parameters = parametersExtractor.extractParameters(upd, inputPattern);
            var commandContext = new CommandActionContext(ctx.user(), command, upd.getUpdateId(),
                    ctx.chatId(), contextId, parameters);
            rabbitTemplate.convertAndSend(upd.getUpdateId().toString(), commandContext);
        } catch (IncorrectInputFormatException ex) {
            ex.setCommand(command.name().toLowerCase());
            logHelper.logIncorrectInputFormat(ex);
            sendHTML(ctx, ex.getLocalizedMessage(messageSource, userLocale(ctx)));
        } catch (Exception ex) {
            logHelper.logBrokerException(ex);
            send(ctx, escape(ex.getMessage()));
        }
    }
}

