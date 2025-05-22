package lubos.multisearch.telegrambot.bot.commands.processing;

import org.telegram.telegrambots.abilitybots.api.objects.MessageContext;

import java.util.regex.Pattern;

@FunctionalInterface
public interface CommandHandler {

    void handleCommand(MessageContext ctx, Pattern inputPattern, String contextId);

}
