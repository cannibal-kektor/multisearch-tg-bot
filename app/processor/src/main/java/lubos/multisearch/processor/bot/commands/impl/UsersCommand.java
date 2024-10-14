package lubos.multisearch.processor.bot.commands.impl;

import lubos.multisearch.processor.bot.commands.CommandProcessor;
import lubos.multisearch.processor.bot.commands.PageableCommand;
import lubos.multisearch.processor.entrypoint.ActionMessage;
import lubos.multisearch.processor.dto.UserDTO;
import lubos.multisearch.processor.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;
import java.util.Locale;

import static lubos.multisearch.processor.bot.commands.Command.USERS;
import static lubos.multisearch.processor.bot.commands.impl.PurgeCommand.PURGE_USER_CALLBACK_FORMAT;
import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.*;


@Component
public class UsersCommand extends CommandProcessor implements PageableCommand {

    static final String LIST_USERS_CALLBACK_FORMAT = "users_page %d";
    static final String USERS_PAGE = "usersPage";
    private static final String LIST_USERS_INFO = "command.users.info";
    private static final String PLURAL = "command.users.plural";
    private static final String UNBAN_BUTTON = "command.users.unban_button";
    private static final String BAN_BUTTON = "command.users.ban_button";
    private static final String PURGE_BUTTON = "command.users.purge_button";

    private final UserService userService;

    public UsersCommand(UserService userService) {
        super(USERS, LIST_USERS_INFO, ADMIN);
        this.userService = userService;
    }


    @Override
    public void processCommand(ActionMessage actionMessage) {
        switch (actionMessage.contextId()) {
            case MESSAGE, MENU_CALLBACK -> handle(actionMessage, pageConfigs().page());
            case PAGING_CALLBACK -> {
                int pageNum = Integer.parseInt(actionMessage.params().get(USERS_PAGE));
                Pageable pageable = pageConfigs().page().withPage(pageNum);
                handle(actionMessage, pageable);
            }
        }
    }

    private void handle(ActionMessage actionMessage, Pageable pageable) {
        var page = userService.listUsers(pageable);
        Locale locale = userLocale(actionMessage);
        String result = pageToString(page, locale);
        var keyboard = formKeyboard(page, actionMessage.user().getId(), locale);
        sender.send(actionMessage.chatId(), result, keyboard);
    }

    private List<InlineKeyboardRow> formKeyboard(Page<UserDTO> page, Long userId, Locale locale) {
        var rows = page.map(userDTO -> Pair.of(userDTO.banned() ?
                                keyboard.button(UNBAN_BUTTON,
                                        UnbanCommand.UNBAN_USER_CALLBACK_FORMAT.formatted(userDTO.username(), page.getNumber()),
                                        locale, userDTO.username()) :
                                keyboard.button(BAN_BUTTON,
                                        BanCommand.BAN_USER_CALLBACK_FORMAT.formatted(userDTO.username(), page.getNumber()),
                                        locale, userDTO.username()),
                        keyboard.button(PURGE_BUTTON,
                                PURGE_USER_CALLBACK_FORMAT.formatted(userDTO.username(), page.getNumber()),
                                locale, userDTO.username())))
                .map(buttons -> new InlineKeyboardRow(buttons.getFirst(), buttons.getSecond()))
                .toList();
        return formPageableKeyboard(page, rows, userId, locale);
    }


    @Override
    public PageConfig pageConfigs() {
        return new PageConfig(LIST_USERS_CALLBACK_FORMAT,
                PageRequest.of(0, 1, Sort.by("documentsSummary.totalDocumentsSize").descending()),
                PLURAL);
    }
}
