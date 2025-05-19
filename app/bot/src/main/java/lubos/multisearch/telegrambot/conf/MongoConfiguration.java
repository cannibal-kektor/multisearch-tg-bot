package lubos.multisearch.telegrambot.conf;

import com.mongodb.MongoCredential;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

@Configuration
public class MongoConfiguration {

    @Bean
    MongoClientSettingsBuilderCustomizer mongoClientSettingsBuilderCustomizer() {
        return clientSettingsBuilder -> clientSettingsBuilder
                .credential(MongoCredential.createMongoX509Credential());
    }

    @Bean
    MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }


    //MONGO- УЖЕ ЕСТЬ КАКОЙ-ТО ВСТРОЕННЫЙ КОННЕКШЕН ПУЛ ДЛЯ КЛИЕНТА (НО НЕ НАСТРАИВАЕТСЯ ЧЕРЕЗ application.yml)
//    @Bean
//    MongoClientSettingsBuilderCustomizer mongoClientSettingsBuilderCustomizer(){
//        return clientSettingsBuilder -> {
//            clientSettingsBuilder.applyToConnectionPoolSettings(
//                    builder -> {
//                        builder.maxConnecting();
//                        builder.maxSize();
//                        builder.maxConnectionLifeTime()
//                        builder.maxConnectionIdleTime()
//                                ...
//                        //https://www.mongodb.com/docs/manual/reference/connection-string/#connections-connection-options
//                    }
//            )
//        }
//    }
}
