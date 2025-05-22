package lubos.multisearch.telegrambot.bot.commands.processing;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.regex.Pattern;

@FunctionalInterface
public interface ParametersExtractor {

    Map<String, String> extractParameters(Update update, Pattern pattern);

}
