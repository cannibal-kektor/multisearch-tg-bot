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

import static lubos.multisearch.processor.bot.commands.Command.UNBAN;
import static lubos.multisearch.processor.bot.commands.impl.UsersCommand.LIST_USERS_CALLBACK_FORMAT;
import static lubos.multisearch.processor.bot.commands.impl.UsersCommand.USERS_PAGE;
import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.*;


@Component
public class UnbanCommand extends CommandProcessor {

    static final String UNBAN_USER_CALLBACK_FORMAT = "unban %s users %d";
    private static final String UNBAN_INFO = "command.unban.info";
    private static final String UNBANNED_SUCCESSFUL = "command.unban.success";
    private static final String ALREADY_UNBANNED = "command.unban.already_unbanned";
    private static final String BACK_TO_USERS_BUTTON = "command.unban.back_to_users_button";

    private final UserService userService;

    public UnbanCommand(UserService userService) {
        super(UNBAN, UNBAN_INFO, ADMIN);
        this.userService = userService;
    }

    @Override
    public void process(CommandActionContext context) {
        String username = stripTag(context.params().get(USERNAME));
        String result = userService.unbanUser(username) ?
                UNBANNED_SUCCESSFUL : ALREADY_UNBANNED;
        Locale locale = userLocale(context);
        var keyboard = formKeyboard(context.params(), context.user().getId(), locale);
        sender.send(context.chatId(), message(result, locale), keyboard);
    }


    private List<InlineKeyboardRow> formKeyboard(Map<String, String> params,
                                                 Long userId, Locale locale) {
        List<InlineKeyboardRow> rows = new ArrayList<>();
        int usersPageNum = params.containsKey(USERS_PAGE) ?
                Integer.parseInt(params.get(USERS_PAGE)) : -1;

        if (usersPageNum != -1) {
            rows.add(keyboard.singleButtonRow(BACK_TO_USERS_BUTTON,
                    LIST_USERS_CALLBACK_FORMAT.formatted(usersPageNum),
                    locale));
        }
        rows.addAll(keyboard.commandsKeyboard(userId, locale));
        return rows;
    }
}
