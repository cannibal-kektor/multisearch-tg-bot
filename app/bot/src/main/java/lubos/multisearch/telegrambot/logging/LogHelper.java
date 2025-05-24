package lubos.multisearch.telegrambot.logging;

import lombok.extern.slf4j.Slf4j;
import lubos.multisearch.telegrambot.bot.commands.exception.IncorrectInputFormatException;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

import static lubos.multisearch.telegrambot.bot.utils.TelegramHelperUtils.escape;
import static org.telegram.telegrambots.abilitybots.api.objects.Flag.MESSAGE;

@Slf4j
@Component
public class LogHelper {

    public static final String UPDATE_RECEIVED = "Telegram update received. Input: {}";
    public static final String UPDATE_PROCESSING_FINISHED = "Telegram update processing finished";
    public static final String STARTING_COMMAND_HANDLER = "Start Command handler processing";
    public static final String FINISHED_COMMAND_HANDLER = "Finished Command handler processing";
    public static final String INCORRECT_COMMAND_INPUT = "Incorrect user input : {}";
    public static final String MESSAGE_SENDING_BROKER_ERROR = "Error while sending message to broker: [{}]";
    public static final String BOT_INITIALIZING = "Telegram Bot Initializing";
    public static final String BOT_INITIALIZED = "Telegram Bot Initialized";
    public static final String MENU_COMMANDS_SET = "Telegram commands have been set";

    public static final String UPDATE_ID = "telegram.update_id";
    public static final String CHAT_ID = "telegram.chat_id";
    public static final String USERNAME = "telegram.username";
    public static final String COMMAND = "telegram.command";
    public static final String CONTEXT_ID = "telegram.context_id";

    final MessageSource messageSource;

    public LogHelper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void logBotInitializing() {
        log.atError()
                .setMessage(BOT_INITIALIZING)
                .log();
    }

    public void logBotInitialized() {
        log.atError()
                .setMessage(BOT_INITIALIZED)
                .log();
    }

    public void logMenuCommandsConfigured() {
        log.atError()
                .setMessage(MENU_COMMANDS_SET)
                .log();
    }

    public void logUpdateReceived(Update update) {
        log.atError()
                .setMessage(UPDATE_RECEIVED)
                .addArgument(() -> MESSAGE.test(update) ?
                        escape(update.getMessage().getText())
                        : update.getCallbackQuery().getData())
                .log();
    }

    public void logUpdateProcessed(Update update) {
        log.atError()
                .setMessage(UPDATE_PROCESSING_FINISHED)
                .log();
    }


    public void logCommandHandlerStarted() {
        log.atError()
                .setMessage(STARTING_COMMAND_HANDLER)
                .log();

    }

    public void logCommandHandlerFinished() {
        log.atError()
                .setMessage(FINISHED_COMMAND_HANDLER)
                .log();
    }


    public void logIncorrectInputFormat(IncorrectInputFormatException ex) {
        log.atError()
                .setMessage(INCORRECT_COMMAND_INPUT)
                .addArgument(() -> ex.getLocalizedMessage(messageSource, Locale.ENGLISH))
                .setCause(ex)
                .log();
    }

    public void logBrokerException(Exception ex) {
        log.atError()
                .setMessage(MESSAGE_SENDING_BROKER_ERROR)
                .addArgument(ex::getMessage)
                .setCause(ex)
                .log();
    }

}
