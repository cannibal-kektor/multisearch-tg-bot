package lubos.multisearch.telegrambot.dto;

import lubos.multisearch.telegrambot.bot.commands.Command;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Map;

public record ActionMessage(User user,
                            Command command,
                            Long chatId,
                            String contextId,
                            Map<String,String> params) {
}
