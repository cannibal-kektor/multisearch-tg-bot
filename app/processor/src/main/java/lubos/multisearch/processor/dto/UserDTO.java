package lubos.multisearch.processor.dto;

import lombok.Builder;
import org.springframework.context.MessageSource;
import org.springframework.util.unit.DataSize;

import java.util.Locale;

import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.*;


@Builder
public record UserDTO(
        Long telegramId,
        String username,
        boolean admin,
        boolean banned,
        UserDocumentsSummaryDto documentsSummary
) implements Localizable {

    private static final String USER_DTO_FORMAT = "dto.user";

    @Override
    public String toLocalizedString(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(USER_DTO_FORMAT,
                new Object[]{escape(username), telegramId,
                        admin, banned, documentsSummary.totalFiles(),
                        DataSize.ofBytes(documentsSummary.totalDocumentsSize()).toKilobytes(),
                        documentsSummary.maxDocumentSize != null ?
                                DataSize.ofBytes(documentsSummary.maxDocumentSize()).toKilobytes() : 0},
                locale);
    }

    public record UserDocumentsSummaryDto(Long telegramId,
                                          Integer totalFiles,
                                          Long totalDocumentsSize,
                                          Long maxDocumentSize) {

    }
}
