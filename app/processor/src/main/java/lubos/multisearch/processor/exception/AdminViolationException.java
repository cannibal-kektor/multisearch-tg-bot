package lubos.multisearch.processor.exception;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;

import java.util.Locale;

@AllArgsConstructor
public class AdminViolationException extends ApplicationException {

    private static final String ADMIN_TRIED_TO_BAN_ADMIN_OR_CREATOR = "exception.admin_ban_violation";

    private final String adminUsername;
    private final String supposedToBan;

    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(ADMIN_TRIED_TO_BAN_ADMIN_OR_CREATOR, new Object[]{adminUsername, supposedToBan}, locale);
    }
}
