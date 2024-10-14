package lubos.multisearch.processor.mapping;


import lubos.multisearch.processor.dto.RegistrationRequestDTO;
import lubos.multisearch.processor.model.mongo.TelegramUser;
import lubos.multisearch.processor.model.mongo.UserRegistrationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapConfig.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    TelegramUser toUser(UserRegistrationRequest registrationRequest);

    RegistrationRequestDTO toDTO(UserRegistrationRequest registrationRequest);
}

