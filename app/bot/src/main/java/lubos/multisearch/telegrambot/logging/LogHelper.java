package lubos.multisearch.telegrambot.logging;

import lombok.extern.slf4j.Slf4j;
import lubos.multisearch.telegrambot.bot.commands.TelegramCommand;
//import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.telegram.telegrambots.abilitybots.api.objects.Flag.MESSAGE;

@Component
@Slf4j
public class LogHelper {

    public static final String RECEIVED_TG_MESSAGE = "logging.receive.message";
    public static final String RECEIVED_TG_CALLBACK = "logging.receive.callback";
    public static final String STARTING_PREPROCESSING_INPUT_MESSAGE = "logging.preprocess.message.start";
    public static final String STARTING_PREPROCESSING_INPUT_CALLBACK = "logging.preprocess.callback.start";
    public static final String BOT_INITIALIZED = "logging.bot.initialized";
    public static final String MENU_COMMANDS_SET = "logging.menu.commands.set";

    public void logUpdates(List<Update> updates) {
        for (Update update : updates) {
            boolean isMessage = MESSAGE.test(update);
            if (isMessage) {
                var msg = update.getMessage();
                log.atError()
                        .setMessage(RECEIVED_TG_MESSAGE)
                        .addKeyValue("userId", msg.getFrom()::getId)
                        .addKeyValue("username", msg.getFrom()::getUserName)
                        .addKeyValue("messageId", msg::getMessageId)
                        .addKeyValue("message", msg::getText)
                        .log();
            } else {
                var callback = update.getCallbackQuery();
//            log.atDebug()
                log.atError()
                        .setMessage(RECEIVED_TG_CALLBACK)
                        .addKeyValue("userId", callback.getFrom()::getId)
                        .addKeyValue("username", callback.getFrom()::getUserName)
                        .addKeyValue("messageId", callback::getId)
                        .addKeyValue("message", callback::getData)
                        .log();
            }
        }
    }

//    @Before(value = "execution(* lubos.multisearch.telegrambot.bot.commands.TelegramCommand.handleCommand(..)) && this(command) && args(ctx,*,contextId)", argNames = "command,ctx,contextId")
    public void logUpdate(TelegramCommand command, MessageContext ctx, String contextId) {
        Update update = ctx.update();
        boolean isMessage = MESSAGE.test(update);
        if (isMessage) {
            var msg = update.getMessage();
            log.atError()
                    .setMessage(STARTING_PREPROCESSING_INPUT_MESSAGE)
                    .addKeyValue("userId", msg.getFrom()::getId)
                    .addKeyValue("username", msg.getFrom()::getUserName)
                    .addKeyValue("messageId", msg::getMessageId)
                    .addKeyValue("message", msg::getText)
                    .addKeyValue("command", command.command().toString())
                    .addKeyValue("contextId", contextId)
                    .log();
        } else {
            var callback = update.getCallbackQuery();
//            log.atDebug()
            log.atError()
                    .setMessage(STARTING_PREPROCESSING_INPUT_CALLBACK)
                    .addKeyValue("userId", callback.getFrom()::getId)
                    .addKeyValue("username", callback.getFrom()::getUserName)
                    .addKeyValue("messageId", callback::getId)
                    .addKeyValue("message", callback::getData)
                    .addKeyValue("command", command.command().toString())
                    .addKeyValue("contextId", contextId)
                    .log();
        }
    }


    public void logBotInitialized() {
        log.atError()
                .setMessage(BOT_INITIALIZED)
                .log();
    }

    public void menuCommandsSet() {
        log.atError()
                .setMessage(BOT_INITIALIZED)
                .log();
    }
}
