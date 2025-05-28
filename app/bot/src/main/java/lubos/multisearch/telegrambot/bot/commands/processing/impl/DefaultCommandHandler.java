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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.objects.MessageContext;

import java.util.Map;
import java.util.regex.Pattern;

import static lubos.multisearch.telegrambot.bot.utils.TelegramHelperUtils.*;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Scope(SCOPE_PROTOTYPE)
@Primary
@Component
@Accessors(chain = true)
public class DefaultCommandHandler implements CommandHandler {

    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    MessageSource messageSource;
    @Autowired
    LogHelper logHelper;
    @Setter
    @Autowired
    public ParametersExtractor parametersExtractor;
    @Setter
    @Getter
    public Command command;

    @Override
    public void handleCommand(MessageContext ctx, Pattern inputPattern, String contextId) {
        try {
            var parameters = parametersExtractor.extractParameters(ctx.update(), inputPattern);
            sendMsg(ctx, parameters, contextId);
        } catch (IncorrectInputFormatException ex) {
            ex.setCommand(command.name().toLowerCase());
            logHelper.logIncorrectInputFormat(ex);
            sendHTML(ctx, ex.getLocalizedMessage(messageSource, userLocale(ctx)));
        } catch (Exception ex) {
            logHelper.logBrokerException(ex);
            send(ctx, escape(ex.getMessage()));
        }
    }

    void sendMsg(MessageContext ctx, Map<String, String> params, String contextId) {
        var updateId = ctx.update().getUpdateId();
        var commandContext = new CommandActionContext(ctx.user(), command, updateId,
                ctx.chatId(), contextId, params);
        rabbitTemplate.convertAndSend(updateId.toString(), commandContext);
    }

}

