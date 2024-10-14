package lubos.multisearch.processor.bot.commands.impl;

import lubos.multisearch.processor.bot.commands.Command;
import lubos.multisearch.processor.bot.commands.CommandProcessor;
import lubos.multisearch.processor.bot.commands.helper.TelegramUtils;
import lubos.multisearch.processor.entrypoint.ActionMessage;
import lubos.multisearch.processor.service.DocumentService;
import lubos.multisearch.processor.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class PurgeCommand extends CommandProcessor {

    static final String PURGE_USER_CALLBACK_FORMAT = "purge %s users %d";
    private static final String PURGE_INFO = "command.purge.info";
    private static final String USER_NO_DOCUMENTS = "command.purge.no_documents";
    private static final String PURGED_SUCCESSFUL = "command.purge.success";
    private static final String BACK_TO_USERS = "command.purge.back_to_users";

    private final DocumentService documentService;
    private final UserService userService;

    public PurgeCommand(DocumentService documentService, UserService userService) {
        super(Command.PURGE, PURGE_INFO, CREATOR);
        this.documentService = documentService;
        this.userService = userService;
    }

    @Override
    public void processCommand(ActionMessage actionMessage) {
        var parameters = actionMessage.params();
        String username = TelegramUtils.stripTag(parameters.get(USERNAME));
        Long telegramId = userService.fetchTelegramId(username);
        String result = documentService.purgeUser(telegramId, username) ?
                PURGED_SUCCESSFUL : USER_NO_DOCUMENTS;
        Locale locale = TelegramUtils.userLocale(actionMessage);
        var keyboard = formKeyboard(parameters, actionMessage.user().getId(), locale);
        sender.send(actionMessage.chatId(), message(result, locale), keyboard);
    }

    private List<InlineKeyboardRow> formKeyboard(Map<String, String> params,
                                                 Long userId, Locale locale) {
        List<InlineKeyboardRow> rows = new ArrayList<>();
        int usersPageNum = params.containsKey(UsersCommand.USERS_PAGE) ?
                Integer.parseInt(params.get(UsersCommand.USERS_PAGE)) : -1;

        if (usersPageNum != -1) {
            rows.add(keyboard.singleButtonRow(BACK_TO_USERS,
                    UsersCommand.LIST_USERS_CALLBACK_FORMAT.formatted(usersPageNum), locale));
        }
        rows.addAll(keyboard.commandsKeyboard(userId, locale));
        return rows;
    }

}
