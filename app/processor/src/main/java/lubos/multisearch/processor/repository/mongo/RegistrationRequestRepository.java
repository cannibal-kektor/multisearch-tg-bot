package lubos.multisearch.processor.repository.mongo;

import lubos.multisearch.processor.model.mongo.UserRegistrationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface RegistrationRequestRepository extends MongoRepository<UserRegistrationRequest, String> {

    boolean existsByTelegramId(Long telegramId);

    Page<UserRegistrationRequest> findAllBy(Pageable pageable);

    Optional<UserRegistrationRequest> findByUsername(String username);
}
