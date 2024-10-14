package lubos.multisearch.processor.exception;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.*;


@AllArgsConstructor
public class DocumentTypeNotSupported extends ApplicationException{

    private static final String DOCUMENT_TYPE_NOT_SUPPORTED = "exception.document_type_not_supported";

    private final String fileName;

    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(DOCUMENT_TYPE_NOT_SUPPORTED, new Object[]{escape(fileName)}, locale);
    }
}
