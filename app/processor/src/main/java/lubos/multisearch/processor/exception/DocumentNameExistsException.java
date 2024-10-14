package lubos.multisearch.processor.exception;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.*;


@AllArgsConstructor
public class DocumentNameExistsException extends ApplicationException{

    private static final String DOCUMENT_NAME_ALREADY_EXISTS = "exception.document_name_already_exists";

    private final String fileName;

    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(DOCUMENT_NAME_ALREADY_EXISTS, new Object[]{escape(fileName)}, locale);
    }
}
