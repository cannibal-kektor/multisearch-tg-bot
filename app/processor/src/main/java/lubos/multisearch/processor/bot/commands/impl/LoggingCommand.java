package lubos.multisearch.processor.bot.commands.impl;

import lubos.multisearch.processor.bot.commands.Command;
import lubos.multisearch.processor.bot.commands.CommandProcessor;
import lubos.multisearch.processor.entrypoint.CommandActionContext;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.userLocale;

@Component
public class LoggingCommand extends CommandProcessor {

    private static final String LOGGING_INFO = "command.logging.info";
    private static final String CHOOSE_LOGGING_LEVEL = "command.logging.choose_level";
    private static final String LOGGING_LEVEL_SET_SUCCESS = "command.logging.success";
    private static final String LOGGING_CHANGE_CALLBACK_FORMAT = "logging %s";
    private static final String LOGGING_CALLBACK = "LOGGING_CALLBACK";
    private static final String LEVEL = "level";

    final LoggingSystem loggingSystem;

    public LoggingCommand() {
        super(Command.LOGGING, LOGGING_INFO, CREATOR);
        this.loggingSystem = LoggingSystem.get(getClass().getClassLoader());
    }

    @Override
    public void process(CommandActionContext context) {
        switch (context.contextId()) {
            case MESSAGE -> handleMessage(context);
            case LOGGING_CALLBACK -> handleCallback(context, context.params());
        }
    }

    private void handleMessage(CommandActionContext context) {
        var locale = userLocale(context);
        var levelChooseKeyboard = formKeyboard();
        sender.send(context.chatId(), message(CHOOSE_LOGGING_LEVEL, locale), levelChooseKeyboard);
    }

    private void handleCallback(CommandActionContext context, Map<String, String> parameters) {
        String level = parameters.get(LEVEL);
        LogLevel logLevel = LogLevel.valueOf(level);
        var locale = userLocale(context);
        loggingSystem.setLogLevel(null, logLevel);
        sender.send(context.chatId(), message(LOGGING_LEVEL_SET_SUCCESS, locale), keyboard.commandsKeyboard(context.user().getId(), locale));
    }

    private List<InlineKeyboardRow> formKeyboard() {
        List<InlineKeyboardButton> rows = new ArrayList<>();
        for (LogLevel level : LogLevel.values()) {
            rows.add(keyboard.button(level.name(), LOGGING_CHANGE_CALLBACK_FORMAT.formatted(level.name())));
        }
        return List.of(new InlineKeyboardRow(rows) );
    }
}
