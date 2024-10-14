package lubos.multisearch.telegrambot.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lubos.multisearch.telegrambot.repository.TelegramUserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    final TelegramUserRepository userRepository;

    @Getter
    volatile Set<Long> adminsIds;
    @Getter
    volatile Set<Long> bannedIds;

    public UserService(TelegramUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public boolean checkUser(User user) {
        String userName = user.getUserName();
        if (userName != null) {
            Long id = user.getId();
            var username = userName.toLowerCase();
            var mongoTgUser = userRepository.findByTelegramId(id);
            if (mongoTgUser != null) {
                if (!username.equals(mongoTgUser.username())) {
                    userRepository.findAndUpdateUsernameByTelegramId(id, username);
                }
                if (mongoTgUser.languageCode() != null) {
                    user.setLanguageCode(mongoTgUser.languageCode());
                }
                return true;
            }
        }
        return false;
    }

    @Transactional(readOnly = true)
    @PostConstruct
    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.SECONDS)
    public void refresh() {
        bannedIds = userRepository.findBannedIds();
        adminsIds = userRepository.findAdminIds();
    }


}