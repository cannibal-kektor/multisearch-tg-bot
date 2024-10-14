package lubos.multisearch.processor.dto;

import lombok.Builder;
import lubos.multisearch.processor.model.DocumentType;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.*;


@Builder
public record ChapterDTO(
        String id,
        String documentName,
        String documentId,
        String userName,
        String chapterPath,
        String title,
        Integer serialNumber,
        DocumentType type,
        String content
) implements Localizable {

    private static final String CHAPTER_FILE_DTO_FORMAT = "dto.chapter.file_format";
    private static final String CHAPTER_HTML_DTO_FORMAT = "dto.chapter.html_format";

    @Override
    public String toLocalizedString(MessageSource messageSource, Locale locale) {
        return switch (type) {
            case FILE -> messageSource.getMessage(CHAPTER_FILE_DTO_FORMAT,
                    new Object[]{escape(documentName), escape(title),
                            escape(chapterPath), serialNumber,
                            id, documentId, content}, locale);
            case HTML -> messageSource.getMessage(CHAPTER_HTML_DTO_FORMAT,
                    new Object[]{documentName, title != null ? escape(title) : "-",
                            id, documentId, content}, locale);
        };
    }
}
