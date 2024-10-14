package lubos.multisearch.processor.entrypoint;

import lubos.multisearch.processor.bot.commands.helper.TelegramKeyboard;
import lubos.multisearch.processor.bot.commands.helper.TelegramSender;
import lubos.multisearch.processor.exception.ApplicationException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Locale;

import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.escape;
import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.userLocale;

@Component("rabbitErrorHandler")
public class ExceptionHandler implements RabbitListenerErrorHandler {

    private final TelegramSender sender;
    private final TelegramKeyboard keyboard;
    private final MessageSource messageSource;

    public ExceptionHandler(TelegramSender sender, TelegramKeyboard keyboard, MessageSource messageSource) {
        this.sender = sender;
        this.keyboard = keyboard;
        this.messageSource = messageSource;
    }

    @SuppressWarnings({"removal"})
    @Override
    public Object handleError(Message amqpMessage, org.springframework.messaging.Message<?> message,
                              ListenerExecutionFailedException ex) {
        Throwable cause = ex.getCause();
        ActionMessage actionMessage = (ActionMessage) message.getPayload();

        switch (cause) {
            case ApplicationException e -> {
                Locale locale = userLocale(actionMessage);
                sender.send(actionMessage.chatId(), e.getLocalizedMessage(messageSource, locale),
                        keyboard.commandsKeyboard(actionMessage.user().getId(), locale));
            }
            case DataIntegrityViolationException e -> sender.send(actionMessage.chatId(), escape(e.getMessage()),
                    keyboard.commandsKeyboard(actionMessage.user().getId(), userLocale(actionMessage)));
            case DataAccessException e -> {
//                    log ...
                throw ex;
            }
            case Throwable e -> {
                sender.send(actionMessage.chatId(), "Something went wrong... Contact admins");
            }
        }
        return null;
    }
}
