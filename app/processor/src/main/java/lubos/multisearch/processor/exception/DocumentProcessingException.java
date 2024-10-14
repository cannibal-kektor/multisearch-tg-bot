package lubos.multisearch.processor.exception;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.escape;


@AllArgsConstructor
public class DocumentProcessingException extends ApplicationException {

    private static final String DOCUMENT_PARSE_EXCEPTION = "exception.document_parsing_failed";

    private final String fileName;

    public DocumentProcessingException(String fileName, Exception cause) {
        super(cause);
        this.fileName = fileName;
    }

    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(DOCUMENT_PARSE_EXCEPTION,
                new Object[]{escape(fileName), escape(getCause().getMessage())}, locale);
    }
}
