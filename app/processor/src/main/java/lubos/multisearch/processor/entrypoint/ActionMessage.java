package lubos.multisearch.processor.entrypoint;

import jakarta.validation.constraints.NotNull;
import lubos.multisearch.processor.bot.commands.Command;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Map;

public record ActionMessage(@NotNull User user,
                            @NotNull Command command,
                            @NotNull Long chatId,
                            @NotNull String contextId,
                            @NotNull Map<String,String> params) {
}
