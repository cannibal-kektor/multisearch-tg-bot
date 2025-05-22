package lubos.multisearch.telegrambot.logging;

import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.telegram.telegrambots.abilitybots.api.objects.Flag.MESSAGE;

@Component
@Slf4j
public class LogHelper {

    public static final String RECEIVED_TG_MESSAGE = "logging.receive.message";
    public static final String RECEIVED_TG_CALLBACK = "logging.receive.callback";
    public static final String STARTING_PREPROCESSING_INPUT_MESSAGE = "logging.update.message.start";
    public static final String FINISHED_PROCESSING_INPUT_MESSAGE = "logging.update.message.finish";
    public static final String STARTING_PREPROCESSING_INPUT_CALLBACK = "logging.update.callback.start";
    public static final String FINISHED_PROCESSING_INPUT_CALLBACK = "logging.update.callback.finish";
    public static final String STARTING_COMMAND_HANDLER = "logging.command.handler.start";
    public static final String FINISHED_COMMAND_HANDLER = "logging.command.handler.finish";

    public static final String BOT_INITIALIZING = "logging.bot.initializing";
    public static final String BOT_INITIALIZED = "logging.bot.initialized";
    public static final String MENU_COMMANDS_SET = "logging.menu.commands.set";



    public void logBotInitializing() {
        log.atInfo()
                .setMessage(BOT_INITIALIZED)
                .log();
    }

    public void logBotInitialized() {
        log.atInfo()
                .setMessage(BOT_INITIALIZING)
                .log();
    }

    public void logUpdateReceived(Update update) {
        boolean isMessage = MESSAGE.test(update);
        if (isMessage) {
            var msg = update.getMessage();
            log.atError()
                    .setMessage(STARTING_PREPROCESSING_INPUT_MESSAGE)
//                    .addKeyValue("userId", msg.getFrom()::getId)
//                    .addKeyValue("username", msg.getFrom()::getUserName)
//                    .addKeyValue("messageId", msg::getMessageId)
//                    .addKeyValue("message", msg::getText)
//                    .addKeyValue("command", command.command().toString())
//                    .addKeyValue("contextId", contextId)
                    .log();
        } else {
            var callback = update.getCallbackQuery();
            log.atError()
                    .setMessage(STARTING_PREPROCESSING_INPUT_CALLBACK)
//                    .addKeyValue("userId", callback.getFrom()::getId)
//                    .addKeyValue("username", callback.getFrom()::getUserName)
//                    .addKeyValue("messageId", callback::getId)
//                    .addKeyValue("message", callback::getData)
//                    .addKeyValue("command", command.command().toString())
//                    .addKeyValue("contextId", contextId)
                    .log();
        }
    }

//             .addKeyValue("userId", msg.getFrom()::getId)
//            .addKeyValue("username", msg.getFrom()::getUserName)
//            .addKeyValue("messageId", msg::getMessageId)
//            .addKeyValue("message", msg::getText)
//            .addKeyValue("command", command.command().toString())
//            .addKeyValue("contextId", contextId)


    public void logUpdateProcessed(Update update) {
        boolean isMessage = MESSAGE.test(update);
        if (isMessage) {
            var msg = update.getMessage();
            log.atError()
                    .setMessage(FINISHED_PROCESSING_INPUT_MESSAGE)
//                    .addKeyValue("userId", msg.getFrom()::getId)
//                    .addKeyValue("username", msg.getFrom()::getUserName)
//                    .addKeyValue("messageId", msg::getMessageId)
//                    .addKeyValue("message", msg::getText)
//                    .addKeyValue("command", command.command().toString())
//                    .addKeyValue("contextId", contextId)
                    .log();
        } else {
            var callback = update.getCallbackQuery();
            log.atError()
                    .setMessage(FINISHED_PROCESSING_INPUT_CALLBACK)
//                    .addKeyValue("userId", callback.getFrom()::getId)
//                    .addKeyValue("username", callback.getFrom()::getUserName)
//                    .addKeyValue("messageId", callback::getId)
//                    .addKeyValue("message", callback::getData)
//                    .addKeyValue("command", command.command().toString())
//                    .addKeyValue("contextId", contextId)
                    .log();
        }
    }

    public void logCommandHandlerStarted(MessageContext messageContext, String contextId) {

        log.atError()
                .setMessage(STARTING_COMMAND_HANDLER + " " + messageContext.user().getUserName())
                .log();
//        boolean isMessage = MESSAGE.test(update);
//        if (isMessage) {
//            var msg = update.getMessage();
//            log.atError()
//                    .setMessage(STARTING_COMMAND_HANDLER)
////                    .addKeyValue("userId", msg.getFrom()::getId)
////                    .addKeyValue("username", msg.getFrom()::getUserName)
////                    .addKeyValue("messageId", msg::getMessageId)
////                    .addKeyValue("message", msg::getText)
////                    .addKeyValue("command", command.command().toString())
////                    .addKeyValue("contextId", contextId)
//                    .log();
//        } else {
//            var callback = update.getCallbackQuery();
//            log.atError()
//                    .setMessage(STARTING_COMMAND_HANDLER)
////                    .addKeyValue("userId", callback.getFrom()::getId)
////                    .addKeyValue("username", callback.getFrom()::getUserName)
////                    .addKeyValue("messageId", callback::getId)
////                    .addKeyValue("message", callback::getData)
////                    .addKeyValue("command", command.command().toString())
////                    .addKeyValue("contextId", contextId)
//                    .log();
//        }
    }

//             .addKeyValue("userId", msg.getFrom()::getId)
//            .addKeyValue("username", msg.getFrom()::getUserName)
//            .addKeyValue("messageId", msg::getMessageId)
//            .addKeyValue("message", msg::getText)
//            .addKeyValue("command", command.command().toString())
//            .addKeyValue("contextId", contextId)


    public void logCommandHandlerFinished(MessageContext messageContext, String contextId) {
        log.atError()
                .setMessage(FINISHED_COMMAND_HANDLER + " " + messageContext.user().getUserName())
                .log();
    }
}
