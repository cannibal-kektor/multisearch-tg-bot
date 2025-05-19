package lubos.multisearch.processor.exception;

import lombok.Getter;
import lombok.Setter;
import lubos.multisearch.processor.entrypoint.ActionMessage;
import org.springframework.context.MessageSource;

import java.util.Locale;


public class ApplicationException extends RuntimeException {

    @Getter
    @Setter
    private ActionMessage actionMessage;

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Exception cause) {
        super(message, cause);
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }

    public ApplicationException() {
    }

    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return getMessage();
    }
}
