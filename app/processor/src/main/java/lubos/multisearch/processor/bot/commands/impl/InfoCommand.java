package lubos.multisearch.processor.bot.commands.impl;

import lubos.multisearch.processor.bot.commands.CommandProcessor;
import lubos.multisearch.processor.entrypoint.ActionMessage;
import lubos.multisearch.processor.bot.commands.Command;
import org.springframework.stereotype.Component;

import java.util.Locale;

import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.*;


@Component
public class InfoCommand extends CommandProcessor {

    private static final String INFO_INFO = "command.info.info";
    private static final String INFO_DESCRIPTION_FULL = "command.info.bot_description";

    public InfoCommand() {
        super(Command.INFO, INFO_INFO, PUBLIC);
    }


    @Override
    public void processCommand(ActionMessage actionMessage) {
        Locale locale = userLocale(actionMessage);
        sender.send(actionMessage.chatId(), message(INFO_DESCRIPTION_FULL, locale),
                keyboard.commandsKeyboard(actionMessage.user().getId(), locale));

    }
}
