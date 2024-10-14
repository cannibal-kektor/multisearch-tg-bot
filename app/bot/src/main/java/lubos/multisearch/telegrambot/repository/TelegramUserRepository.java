package lubos.multisearch.telegrambot.repository;

import lubos.multisearch.telegrambot.model.TelegramUser;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.springframework.data.mongodb.core.query.Query.query;


@Repository
@Transactional(readOnly = true)
public class TelegramUserRepository {

    final MongoTemplate template;

    public TelegramUserRepository(MongoTemplate mongoTemplate) {
        this.template = mongoTemplate;
    }

    public TelegramUser findByTelegramId(Long telegramId) {
        Query query = query(Criteria.where("telegramId").is(telegramId));
        query.fields()
                .include("telegramId")
                .include("username")
                .include("languageCode")
                .exclude("_id");
        return template.query(TelegramUser.class)
                .matching(query)
                .oneValue();
    }

    @Transactional
    public void findAndUpdateUsernameByTelegramId(Long telegramId, String username) {
        template.update(TelegramUser.class)
                .matching(query(Criteria.where("telegramId").is(telegramId)))
                .apply(Update.update("username", username))
                .first();
    }

    public Set<Long> findBannedIds() {
        Query query = query(Criteria.where("banned").is(true));
        query.fields()
                .include("telegramId")
                .exclude("_id");
        return template.query(TelegramUser.class)
                .matching(query)
                .all()
                .stream()
                .map(TelegramUser::telegramId)
                .collect(toSet());
    }

    public Set<Long> findAdminIds() {
        Query query = query(Criteria.where("admin").is(true));
        query.fields()
                .include("telegramId")
                .exclude("_id");
        return template.query(TelegramUser.class)
                .matching(query)
                .all()
                .stream()
                .map(TelegramUser::telegramId)
                .collect(toSet());
    }

}
