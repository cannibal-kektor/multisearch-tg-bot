package lubos.multisearch.processor.dto;

import org.springframework.context.MessageSource;

import java.util.Locale;

public interface Localizable {
    String toLocalizedString(MessageSource messageSource, Locale locale);
}
