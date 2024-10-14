package lubos.multisearch.processor.dto;

import lubos.multisearch.processor.model.DocumentType;
import lombok.Builder;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;

import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.*;
import static java.util.stream.Collectors.joining;


@Builder
public record SearchDTO(
        String id,
        String documentName,
        String userName,
        String chapterPath,
        String title,
        Integer serialNumber,
        DocumentType type,
        List<String> highlights,
        float score
) implements Localizable {

    private static final String SEARCH_FILE_DTO_FORMAT = "dto.search.file_format";
    private static final String SEARCH_HTML_DTO_FORMAT = "dto.search.html_format";
    private static final String HIGHLIGHTS_PREFIX = "dto.search.highlight_prefix";
    private static final String HIGHLIGHT_FORMAT = """
            <b>%d)</b> <blockquote>%s</blockquote>
            """;


    @Override
    public String toLocalizedString(MessageSource messageSource, Locale locale) {
        String mainPart = switch (type) {
            case FILE -> messageSource.getMessage(SEARCH_FILE_DTO_FORMAT,
                    new Object[]{escape(documentName), escape(title),
                            escape(chapterPath), serialNumber,
                            id, score}, locale);
            case HTML -> messageSource.getMessage(SEARCH_HTML_DTO_FORMAT,
                    new Object[]{documentName, escape(title),
                            id, score}, locale);
        };
        int[] highlightNum = {0};
        return mainPart + highlights
                .stream()
//                .map(TgHelperUtils::escape)
                .map(highlight -> HIGHLIGHT_FORMAT.formatted(++highlightNum[0], highlight))
                .collect(joining("", messageSource.getMessage(HIGHLIGHTS_PREFIX, null, locale), ""));
    }
}
