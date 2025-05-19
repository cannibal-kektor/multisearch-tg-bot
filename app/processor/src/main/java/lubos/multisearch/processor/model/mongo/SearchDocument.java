package lubos.multisearch.processor.model.mongo;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lubos.multisearch.processor.model.DocumentType;
import lubos.multisearch.processor.model.validation.DocumentValidation;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;


@Getter
@Setter
@Builder
@AllArgsConstructor
@Document
@CompoundIndex(name = "unique-document-per-user", unique = true, def = "{'user.id': 1, 'documentName': 1}")
public class SearchDocument {

    @Id
    String id;

    @NotNull
    DocumentType documentType;

    @NotEmpty
    @Size(max = 100, groups = DocumentValidation.File.class)
    @URL(groups = DocumentValidation.HTML.class)
    String documentName;

    @NotNull
    @PositiveOrZero
    @Max(value = 20_000_000,
            message = "The file is too large. File size must me less than 20 Mb",
            groups = DocumentValidation.File.class)
    Long size;

    @NotEmpty(groups = DocumentValidation.File.class)
    String telegramFileId;

    @Size(max = 200, groups = DocumentValidation.HTML.class)
    String htmlTitle;

    //Мб я сделал DocumentReference а не DBRef тк на него можно CompoundIndex наложить (telegramId)?
    @NotNull
    @DocumentReference(lazy = true, lookup = "{ 'telegramId' : ?#{telegramId} }")
    TelegramUser user;

}
