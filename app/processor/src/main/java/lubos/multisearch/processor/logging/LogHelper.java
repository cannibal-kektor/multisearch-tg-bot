package lubos.multisearch.processor.logging;

import lombok.extern.slf4j.Slf4j;
import lubos.multisearch.processor.exception.ApplicationException;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Locale;

@Slf4j
@Component
public class LogHelper {


    public static final String COMMAND_RECEIVED = "Telegram command message received";
    public static final String COMMAND_PROCESSING_FINISHED = "Telegram command message processing finished";
    public static final String STARTING_COMMAND_PROCESSOR = "Start Command processor [{}]";
    public static final String FINISHED_COMMAND_PROCESSOR = "Finished Command processor [{}]";
    public static final String FAILED_COMMAND_PROCESSING = "Processing command failed. Reason: [{}]";
    public static final String FAILED_REQUEST_TO_TELEGRAM = "Failed request [{}] to Telegram. Reason: [{}]";
    public static final String FAILED_SENDING_TG_FILE = "Failed sending uploaded file [{}] to user. Reason: [{}]";

    public static final String UPDATE_ID = "telegram.update_id";
    public static final String CHAT_ID = "telegram.chat_id";
    public static final String USERNAME = "telegram.username";
    public static final String COMMAND = "telegram.command";
    public static final String CONTEXT_ID = "telegram.context_id";
    public static final String COMMAND_PROCESSOR = "telegram.command_processor";

    final MessageSource messageSource;

    public LogHelper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void logCommandReceived() {
        log.atDebug()
                .setMessage(COMMAND_RECEIVED)
                .log();
    }

    public void logCommandProcessed() {
        log.atDebug()
                .setMessage(COMMAND_PROCESSING_FINISHED)
                .log();
    }


    public void logCommandProcessorStarted(String processorImpl) {
        log.atDebug()
                .setMessage(STARTING_COMMAND_PROCESSOR)
                .addArgument(processorImpl)
                .log();
    }

    public void logCommandProcessorFinished(String processorImpl) {
        log.atDebug()
                .setMessage(FINISHED_COMMAND_PROCESSOR)
                .addArgument(processorImpl)
                .log();
    }


    public void logFailedProcessing(Throwable ex) {
        log.atError()
                .setMessage(FAILED_COMMAND_PROCESSING)
                .addArgument(() -> switch (ex.getCause()) {
                    case ApplicationException e -> e.getLocalizedMessage(messageSource, Locale.ENGLISH);
                    case Throwable e -> e.getMessage();
                })
                .setCause(ex)
                .log();
    }

    public void logFailedRequestToTelegram(BotApiMethod<?> method, TelegramApiException ex) {
        log.atError()
                .setMessage(FAILED_REQUEST_TO_TELEGRAM)
                .addArgument(method.getMethod())
                .addArgument(ex.getMessage())
                .setCause(ex)
                .log();
    }

    public void logFailSendingTelegramFile(SendDocument sendDocument, TelegramApiException ex) {
        log.atError()
                .setMessage(FAILED_SENDING_TG_FILE)
                .addArgument(sendDocument.getFile().getAttachName())
                .addArgument(ex.getMessage())
                .setCause(ex)
                .log();
    }
}
