package lubos.multisearch.processor.exception;

import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.Set;

import static java.util.stream.Collectors.joining;

@RequiredArgsConstructor
public class ValidationException extends ApplicationException {

    private final Set<ConstraintViolation<Object>> violations;

    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return violations.stream()
                .map(cv -> cv == null ? "null" : cv.getPropertyPath() + ": " + messageSource.getMessage(cv.getMessage(), null, locale))
                .collect(joining(", "));
    }

}
