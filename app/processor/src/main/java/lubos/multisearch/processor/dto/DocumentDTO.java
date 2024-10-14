package lubos.multisearch.processor.dto;

import lubos.multisearch.processor.model.DocumentType;
import lombok.Builder;
import org.springframework.context.MessageSource;
import org.springframework.util.unit.DataSize;

import java.util.Locale;

import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.*;


@Builder
public record DocumentDTO(String id,
                          String documentName,
                          DocumentType documentType,
                          Long size,
                          String telegramFileId,
                          String htmlTitle
) implements Localizable {

    private static final String FILE_DTO_FORMAT = "dto.document.file_format";
    private static final String HTML_DTO_FORMAT = "dto.document.html_format";

    @Override
    public String toLocalizedString(MessageSource messageSource, Locale locale) {
        return switch (documentType) {
            case FILE -> messageSource.getMessage(FILE_DTO_FORMAT,
                    new Object[]{escape(documentName),
                            DataSize.ofBytes(size).toKilobytes(),
                            telegramFileId}, locale);
            case HTML -> messageSource.getMessage(HTML_DTO_FORMAT,
                    new Object[]{documentName,
                            escape(htmlTitle),
                            DataSize.ofBytes(size).toKilobytes()}, locale);
        };
    }
}
