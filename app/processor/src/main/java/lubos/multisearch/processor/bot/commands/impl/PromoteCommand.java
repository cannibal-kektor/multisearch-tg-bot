package lubos.multisearch.processor.bot.commands.impl;

import lubos.multisearch.processor.bot.commands.Command;
import lubos.multisearch.processor.bot.commands.CommandProcessor;
import lubos.multisearch.processor.entrypoint.ActionMessage;
import lubos.multisearch.processor.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Locale;

import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.*;


@Component
public class PromoteCommand extends CommandProcessor {

    private static final String PROMOTE_INFO = "command.promote.info";
    private static final String USER_PROMOTED = "command.promote.success";
    private static final String USER_ALREADY_ADMIN = "command.promote.already_admin";

    private final UserService userService;

    public PromoteCommand(UserService userService) {
        super(Command.PROMOTE, PROMOTE_INFO, CREATOR);
        this.userService = userService;
    }

    @Override
    public void processCommand(ActionMessage actionMessage) {
        String username = stripTag(actionMessage.params().get(USERNAME));
        String result = userService.promoteUser(username) ?
                USER_PROMOTED : USER_ALREADY_ADMIN;
        Locale locale = userLocale(actionMessage);
        sender.send(actionMessage.chatId(), message(result, locale),
                keyboard.commandsKeyboard(actionMessage.user().getId(), locale));

    }

}
