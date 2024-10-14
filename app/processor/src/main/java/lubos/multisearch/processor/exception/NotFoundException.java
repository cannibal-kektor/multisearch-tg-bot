package lubos.multisearch.processor.exception;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.*;


@AllArgsConstructor
public class NotFoundException extends ApplicationException {

    private static final String NOT_FOUND_FORMAT = "exception.not_found";

    private final String entity;

    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(NOT_FOUND_FORMAT, new Object[]{escape(entity)}, locale);
    }
}
