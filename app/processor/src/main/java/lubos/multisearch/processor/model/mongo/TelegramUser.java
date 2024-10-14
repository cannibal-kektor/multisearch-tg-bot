package lubos.multisearch.processor.model.mongo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@AllArgsConstructor
@Builder
@Document
public class TelegramUser {

    @Id
    String id;

    @NotNull
    @Positive
    @Indexed(unique = true)
    Long telegramId;

    @NotEmpty
    @Size(max = 32)
    @Indexed(unique = true)
    String username;

    @Builder.Default
    Boolean admin = false;

    @Builder.Default
    Boolean banned = false;

    LastSearch lastSearch;

    String languageCode;

    public static TelegramUser onlyId(Long telegramId) {
        return builder().telegramId(telegramId).build();
    }

    public record LastSearch(@Size(max = 400) String searchStr,
                             Short pageNum) {
    }

}

