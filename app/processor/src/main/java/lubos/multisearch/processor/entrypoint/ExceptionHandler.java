package lubos.multisearch.processor.entrypoint;

import com.rabbitmq.client.Channel;
import lubos.multisearch.processor.bot.commands.helper.TelegramKeyboard;
import lubos.multisearch.processor.bot.commands.helper.TelegramSender;
import lubos.multisearch.processor.exception.ApplicationException;
import lubos.multisearch.processor.logging.LogHelper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.userLocale;

@Component("rabbitErrorHandler")
public class ExceptionHandler implements RabbitListenerErrorHandler {

    private static final String SERVER_ERROR_MESSAGE = "exception.server_error";

    private final TelegramSender sender;
    private final TelegramKeyboard keyboard;
    private final MessageSource messageSource;
    private final LogHelper logHelper;

    public ExceptionHandler(TelegramSender sender, TelegramKeyboard keyboard, MessageSource messageSource, LogHelper logHelper) {
        this.sender = sender;
        this.keyboard = keyboard;
        this.messageSource = messageSource;
        this.logHelper = logHelper;
    }

    @Override
    public Object handleError(Message amqpMessage, Channel channel, org.springframework.messaging.Message<?> message,
                              ListenerExecutionFailedException ex) {
        var commandContext = (CommandActionContext) message.getPayload();
        var locale = userLocale(commandContext);
        String msg = switch (ex.getCause()) {
            case ApplicationException e -> e.getLocalizedMessage(messageSource, locale);
            default -> messageSource.getMessage(SERVER_ERROR_MESSAGE, null, locale);
        };
        logHelper.logFailedProcessing(ex);
        sender.send(commandContext.chatId(), msg, keyboard.commandsKeyboard(commandContext.user().getId(), locale));
//        throw ex;if we do not throw retry not working
        return null;
    }
}
