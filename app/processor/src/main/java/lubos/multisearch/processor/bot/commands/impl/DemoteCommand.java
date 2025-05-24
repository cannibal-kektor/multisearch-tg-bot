package lubos.multisearch.processor.bot.commands.impl;

import lubos.multisearch.processor.bot.commands.CommandProcessor;
import lubos.multisearch.processor.entrypoint.CommandActionContext;
import lubos.multisearch.processor.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Locale;

import static lubos.multisearch.processor.bot.commands.Command.DEMOTE;
import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.*;


@Component
public class DemoteCommand extends CommandProcessor {

    private static final String DEMOTE_INFO = "command.demote.info";
    private static final String ADMIN_DEMOTED = "command.demote.success";
    private static final String USER_IS_NOT_ADMIN = "command.demote.not_admin";

    private final UserService userService;

    public DemoteCommand(UserService userService) {
        super(DEMOTE, DEMOTE_INFO, CREATOR);
        this.userService = userService;
    }

    @Override
    public void process(CommandActionContext context) {
        String username = stripTag(context.params().get(USERNAME));
        String result = userService.demoteAdmin(username) ?
                ADMIN_DEMOTED : USER_IS_NOT_ADMIN;
        Locale locale = userLocale(context);
        sender.send(context.chatId(), message(result, locale),
                keyboard.commandsKeyboard(context.user().getId(), locale));
    }

}
