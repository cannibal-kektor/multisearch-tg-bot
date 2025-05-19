package lubos.multisearch.processor.conf;

import com.mongodb.MongoCredential;
import lubos.multisearch.processor.model.mongo.schema.SchemaValidationSupplier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.util.TypeInformation;

@Configuration
public class MongoConfiguration {

    @Bean
    MongoClientSettingsBuilderCustomizer mongoClientSettingsBuilderCustomizer() {
        return builder -> builder
                .credential(MongoCredential.createMongoX509Credential());
    }

    @Bean
    MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

//    return new MongoTemplate(databaseFactory, converter);

    @Bean
    MongoTemplate mongoTemplate(MongoDatabaseFactory factory, MappingMongoConverter converter, SchemaValidationSupplier schemaSupplier) {
        //remove _class field (but refuse from entity polymorphism)
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        //        var mappingContext = mongoTemplate
//                .getConverter().getMappingContext();
//        var resolver = new MongoPersistentEntityIndexResolver(mappingContext);
//        mappingContext.getManagedTypes()
//                .stream()
//                .map(TypeInformation::getType)
//                .filter(type -> type.isAnnotationPresent(Document.class))
//                .forEach((entityType) -> {
//                    var schema = schemaSupplier.getSchemaFor(entityType);
//                    mongoTemplate.createCollection(entityType, CollectionOptions.empty().schema(schema));
//                    IndexOperations indexOps = mongoTemplate.indexOps(entityType);
//                    resolver.resolveIndexFor(entityType).forEach(indexOps::ensureIndex);
//                });
        return new MongoTemplate(factory, converter);
    }

    @Bean
    ApplicationRunner delayedMongoCollectionInitializer(MongoTemplate mongoTemplate, SchemaValidationSupplier schemaSupplier) {
        return _ -> {
            var mappingContext = mongoTemplate
                    .getConverter().getMappingContext();
            var resolver = new MongoPersistentEntityIndexResolver(mappingContext);
            mappingContext.getManagedTypes()
                    .stream()
                    .map(TypeInformation::getType)
                    .filter(type -> type.isAnnotationPresent(Document.class))
                    .forEach((entityType) -> {
                        var schema = schemaSupplier.getSchemaFor(entityType);
                        mongoTemplate.createCollection(entityType, CollectionOptions.empty().schema(schema));
                        IndexOperations indexOps = mongoTemplate.indexOps(entityType);
                        resolver.resolveIndexFor(entityType).forEach(indexOps::ensureIndex);
                    });
        };
    }

    //MONGO- УЖЕ ЕСТЬ КАКОЙ-ТО ВСТРОЕННЫЙ КОННЕКШЕН ПУЛ ДЛЯ КЛИЕНТА (НО НЕ НАСТРАИВАЕТСЯ ЧЕРЕЗ application.yml)
//    @Bean
//    MongoClientSettingsBuilderCustomizer mongoClientSettingsBuilderCustomizer(){
//        return clientSettingsBuilder -> {
//            clientSettingsBuilder.applyToConnectionPoolSettings(
//                    builder -> {
//                        builder.maxConnecting();
//                        builder.maxConnectionLifeTime()
//                        builder.maxConnectionIdleTime()
//                                ...
//                        //https://www.mongodb.com/docs/manual/reference/connection-string/#connections-connection-options
//                    }
//            )
//        }
//    }

}
