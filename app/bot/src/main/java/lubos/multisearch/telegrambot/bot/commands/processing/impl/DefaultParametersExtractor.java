package lubos.multisearch.telegrambot.bot.commands.processing.impl;

import lubos.multisearch.telegrambot.bot.commands.exception.IncorrectInputFormatException;
import lubos.multisearch.telegrambot.bot.commands.processing.ParametersExtractor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;
import static org.telegram.telegrambots.abilitybots.api.objects.Flag.CALLBACK_QUERY;


@Component
public class DefaultParametersExtractor implements ParametersExtractor {

    @Override
    public Map<String, String> extractParameters(Update upd, Pattern pattern) {
        Map<String, String> parameters;
        if (pattern != null) {
            String inputText;
            if (CALLBACK_QUERY.test(upd)) {
                inputText = upd.getCallbackQuery().getData();
            } else {
                inputText = upd.getMessage().getText();
            }
            Matcher matcher = pattern.matcher(inputText);
            if (!matcher.matches()) {
                throw new IncorrectInputFormatException(inputText);
            }
            parameters = matcher.namedGroups()
                    .entrySet()
                    .stream()
                    .collect(toMap(Map.Entry::getKey, entry -> matcher.group(entry.getKey())));
        } else {
            parameters = emptyMap();
        }
        return parameters;
    }
}
