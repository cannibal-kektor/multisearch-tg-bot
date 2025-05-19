package lubos.multisearch.processor.exception;

import org.springframework.context.MessageSource;

import java.util.Locale;

public class ServerProcessingException extends ApplicationException{

    private static final String SERVER_ERROR_MESSAGE = "exception.server_error";

    public ServerProcessingException(final String message) {
        super(message);

    }
    public ServerProcessingException(final String message, final Throwable cause) {

    }
    public ServerProcessingException(final Throwable cause) {
        super(cause);
    }

    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(SERVER_ERROR_MESSAGE, null, locale);
    }
}
