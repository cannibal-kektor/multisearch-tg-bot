package lubos.multisearch.processor.bot.commands.impl;

import lubos.multisearch.processor.bot.commands.CommandProcessor;
import lubos.multisearch.processor.entrypoint.CommandActionContext;
import lubos.multisearch.processor.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static lubos.multisearch.processor.bot.commands.Command.BAN;
import static lubos.multisearch.processor.bot.commands.impl.UsersCommand.LIST_USERS_CALLBACK_FORMAT;
import static lubos.multisearch.processor.bot.commands.impl.UsersCommand.USERS_PAGE;
import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.*;


@Component
public class BanCommand extends CommandProcessor {

    static final String BAN_USER_CALLBACK_FORMAT = "ban %s users %d";
    private static final String BAN_INFO = "command.ban.info";
    private static final String ALREADY_BANNED = "command.ban.already_banned";
    private static final String BANNED_SUCCESSFUL = "command.ban.success";
    private static final String BACK_TO_USERS = "command.ban.back_to_users";

    private final UserService userService;

    public BanCommand(UserService userService) {
        super(BAN, BAN_INFO, ADMIN);
        this.userService = userService;
    }

    @Override
    public void process(CommandActionContext context) {
        var parameters = context.params();
        String username = stripTag(parameters.get(USERNAME));
        Long userId = context.user().getId();
        String result = userService.banUser(username, username(context), userId) ?
                BANNED_SUCCESSFUL : ALREADY_BANNED;
        Locale locale = userLocale(context);
        var keyboard = formKeyboard(parameters, userId, locale);
        sender.send(context.chatId(), message(result, locale), keyboard);
    }

    private List<InlineKeyboardRow> formKeyboard(Map<String, String> params, Long userId, Locale locale) {
        List<InlineKeyboardRow> rows = new ArrayList<>();
        int usersPageNum = params.containsKey(USERS_PAGE) ?
                Integer.parseInt(params.get(USERS_PAGE)) : -1;

        if (usersPageNum != -1) {
            rows.add(keyboard.singleButtonRow(BACK_TO_USERS,
                    LIST_USERS_CALLBACK_FORMAT.formatted(usersPageNum), locale));
        }
        rows.addAll(keyboard.commandsKeyboard(userId, locale));
        return rows;
    }
}
