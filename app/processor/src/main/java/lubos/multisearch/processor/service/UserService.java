package lubos.multisearch.processor.service;

import lubos.multisearch.processor.conf.BotInfo;
import lubos.multisearch.processor.dto.RegistrationRequestDTO;
import lubos.multisearch.processor.dto.UserDTO;
import lubos.multisearch.processor.model.mongo.TelegramUser;
import lubos.multisearch.processor.model.mongo.UserRegistrationRequest;
import lubos.multisearch.processor.repository.mongo.DocumentRepository;
import lubos.multisearch.processor.repository.mongo.RegistrationRequestRepository;
import lubos.multisearch.processor.repository.mongo.UserRepository;
import lubos.multisearch.processor.exception.AdminViolationException;
import lubos.multisearch.processor.exception.NotFoundException;
import lubos.multisearch.processor.mapping.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.User;

import static lubos.multisearch.processor.bot.commands.CommandProcessor.*;

@Service
public class UserService {

    final UserRepository userRepository;
    final DocumentRepository documentRepository;
    final RegistrationRequestRepository registrationRequestRepository;
    final UserMapper userMapper;
    final BotInfo botInfo;

    public UserService(UserRepository userRepository, RegistrationRequestRepository registrationRequestRepository,
                       UserMapper userMapper, DocumentRepository documentRepository, BotInfo botInfo) {
        this.userRepository = userRepository;
        this.registrationRequestRepository = registrationRequestRepository;
        this.userMapper = userMapper;
        this.documentRepository = documentRepository;
        this.botInfo = botInfo;
    }

    @Transactional(noRollbackFor = AdminViolationException.class)
    public boolean banUser(String usernameToBan, String bannedBy, Long bannedById) {
        return userRepository.findByUsername(usernameToBan)
                .map(userToBan -> {
                    if (userToBan.getTelegramId().equals(botInfo.creatorId()) ||
                            (userToBan.getAdmin() && !bannedById.equals(botInfo.creatorId()))) {
                        // Protection from abuse (Ban current admin or another admin)
                        userRepository.findAndSetBanByUsername(bannedBy, true);
                        throw new AdminViolationException(bannedBy, usernameToBan);
                    }
                    if (userToBan.getBanned())
                        return false;
                    userRepository.findAndSetBanByUsername(usernameToBan, true);
                    return true;
                })
                .orElseThrow(() -> new NotFoundException(usernameToBan));
    }

    @Transactional
    public boolean unbanUser(String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    if (user.getBanned()) {
                        userRepository.findAndSetBanByTelegramId(user.getTelegramId(), false);
                        return true;
                    }
                    return false;
                })
                .orElseThrow(() -> new NotFoundException(username));
    }

    @Transactional
    public boolean promoteUser(String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    if (!user.getAdmin()) {
                        userRepository.findAndSetAdminByTelegramId(user.getTelegramId(), true);
                        return true;
                    }
                    return false;
                })
                .orElseThrow(() -> new NotFoundException(username));
    }

    @Transactional
    public boolean demoteAdmin(String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    if (user.getAdmin()) {
                        userRepository.findAndSetAdminByTelegramId(user.getTelegramId(), false);
                        return true;
                    }
                    return false;
                })
                .orElseThrow(() -> new NotFoundException(username));
    }

    @Transactional(readOnly = true)
    public boolean userExists(Long id) {
        return userRepository.existsByTelegramId(id);
    }

    @Transactional(readOnly = true)
    public int getUserPrivacy(Long tgId) {
        if (tgId == botInfo.creatorId()) {
            return CREATOR;
        }
        return userRepository.existsByTelegramIdAndAdminTrue(tgId) ? ADMIN : PUBLIC;
    }

    @Transactional
    public void createRequestForRegistration(User user, Long chatId) {
        if (!registrationRequestRepository.existsByTelegramId(user.getId())) {
            var registrationRequest = UserRegistrationRequest.builder()
                    .telegramId(user.getId())
                    .username(user.getUserName().toLowerCase())
                    .chatId(chatId)
                    .build();
            registrationRequestRepository.insert(registrationRequest);
        }
    }

    @Transactional
    public void createUser(User user) {
        userRepository.insert(TelegramUser.builder()
                .telegramId(user.getId())
                .username(user.getUserName().toLowerCase())
//                .banned(false)
//                .admin(false)
                .build());
    }

    @Transactional
    public Long registerUser(String username) {
        return registrationRequestRepository.findByUsername(username)
                .map(regRequest -> {
                    registrationRequestRepository.deleteById(regRequest.getId());
                    userRepository.insert(userMapper.toUser(regRequest));
                    return regRequest;
                })
                .map(UserRegistrationRequest::getChatId)
                .orElseThrow(() -> new NotFoundException(username));
    }


    @Transactional(readOnly = true)
    public Page<RegistrationRequestDTO> listUserRegistrationRequests(Pageable pageable) {
        return registrationRequestRepository.findAllBy(pageable)
                .map(userMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> listUsers(Pageable pageable) {
        long totalUsers = userRepository.count();
        var slice = userRepository.findUsersWithDocumentSummary(pageable);
        return new PageImpl<>(slice.getContent(), pageable, totalUsers);
    }

    @Transactional(readOnly = true)
    public Long fetchTelegramId(String username) {
        return userRepository.findTelegramIdByUsername(username)
                .orElseThrow(() -> new NotFoundException(username));
    }

    @Transactional
    public void setUserLanguage(Long telegramId, String lang) {
        userRepository.findAndSetUserLanguageByTelegramId(telegramId, lang);
    }

}