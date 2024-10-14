package lubos.multisearch.processor.exception;

import org.springframework.context.MessageSource;

import java.util.Locale;

public class ApplicationException extends RuntimeException {

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Exception cause) {
        super(message, cause);
    }

    public ApplicationException(Exception cause) {
        super(cause);
    }

    public ApplicationException() {
    }

    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return getMessage();
    }
}
