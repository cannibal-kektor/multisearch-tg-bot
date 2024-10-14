package lubos.multisearch.processor.dto;

import lubos.multisearch.processor.model.DocumentType;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.*;


public record ChapterCompressedDTO(
        String id,
        String documentName,
        String documentId,
        String chapterPath,
        String title,
        DocumentType type
) implements Localizable {

    private static final String CHAPTER_DTO_COMPRESSED_FORMAT = "dto.chapter_compressed";

    @Override
    public String toLocalizedString(MessageSource messageSource, Locale locale) {
        return switch (type) {
            case FILE -> messageSource.getMessage(CHAPTER_DTO_COMPRESSED_FORMAT,
                    new Object[]{escape(title), escape(chapterPath), id}, locale);
            case HTML -> messageSource.getMessage(CHAPTER_DTO_COMPRESSED_FORMAT,
                    new Object[]{escape(title), escape(title), id}, locale);
        };
    }
}
