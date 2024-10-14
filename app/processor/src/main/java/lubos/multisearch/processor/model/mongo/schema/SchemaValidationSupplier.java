package lubos.multisearch.processor.model.mongo.schema;

import org.springframework.data.mongodb.core.schema.MongoJsonSchema;
import org.springframework.stereotype.Component;

import static org.springframework.data.mongodb.core.schema.JsonSchemaProperty.*;

@Component
public class SchemaValidationSupplier {

    public MongoJsonSchema getSchemaFor(Class<?> entityType) {
        return switch (entityType.getSimpleName()) {
            case "SearchDocument" -> getDocumentSchema();
            case "TelegramUser" -> getTelegramUserSchema();
            case "UserRegistrationRequest" -> getUserRegistrationRequestSchema();
            default -> throw new IllegalArgumentException();
        };
    }

    public MongoJsonSchema getDocumentSchema() {
        return MongoJsonSchema.builder()
                .properties(
                        required(string("documentType")),
                        required(string("documentName").minLength(1).maxLength(100)),
                        required(int64("size").gte(0)),
                        string("telegramFileId").maxLength(100),
                        string("htmlTitle").maxLength(200),
                        required(object("user")
                                .properties(required(int64("telegramId"))))
                ).build();
    }


    public MongoJsonSchema getTelegramUserSchema() {
        return MongoJsonSchema.builder()
                .properties(
                        required(string("username").maxLength(32)),
                        required(int64("telegramId")),
                        required(bool("admin")),
                        required(bool("banned")),
                        string("languageCode"),
                        object("lastSearch")
                                .properties(string("searchStr").maxLength(400)))
                .build();
    }

    public MongoJsonSchema getUserRegistrationRequestSchema() {
        return MongoJsonSchema.builder()
                .properties(
                        required(string("username").maxLength(32)),
                        required(int64("telegramId")),
                        required(int64("chatId")))
                .build();
    }


}
