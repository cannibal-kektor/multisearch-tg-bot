package lubos.multisearch.telegrambot.bot.commands.processing.impl;

import lubos.multisearch.telegrambot.bot.commands.CommandsConstants;
import lubos.multisearch.telegrambot.bot.commands.exception.IncorrectInputFormatException;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.objects.MessageContext;

import java.util.Map;
import java.util.regex.Pattern;

import static lubos.multisearch.telegrambot.bot.utils.TelegramHelperUtils.*;

@Component
public class LoggingConfiguringCommandHandler extends DefaultCommandHandler {

    private static final String LEVEL = "level";
    private static final String RESET = "reset";

    final LoggingSystem loggingSystem = LoggingSystem.get(getClass().getClassLoader());

    @Override
    public void handleCommand(MessageContext ctx, Pattern inputPattern, String contextId) {
        try {
            var parameters = parametersExtractor.extractParameters(ctx.update(), inputPattern);
            if (CommandsConstants.LOGGING_CALLBACK.equals(contextId)) {
                configureLogging(parameters);
            }
            super.sendMsg(ctx, parameters, contextId);
        } catch (IncorrectInputFormatException ex) {
            ex.setCommand(command.name().toLowerCase());
            logHelper.logIncorrectInputFormat(ex);
            sendHTML(ctx, ex.getLocalizedMessage(messageSource, userLocale(ctx)));
        } catch (Exception ex) {
            logHelper.logBrokerException(ex);
            send(ctx, escape(ex.getMessage()));
        }
    }

    void configureLogging(Map<String, String> parameters) {
        String level = parameters.get(LEVEL);
        LogLevel logLevel = !level.equals(RESET) ? LogLevel.valueOf(level) : null;
        loggingSystem.setLogLevel(null, logLevel);
    }
}
