package lubos.multisearch.processor.exception;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.*;

@AllArgsConstructor
public class FileTooBigException extends ApplicationException {

    private static final String FILE_IS_TOO_BIG = "exception.file_is_too_big";

    private final String fileName;

    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(FILE_IS_TOO_BIG, new Object[]{escape(fileName)}, locale);
    }
}
