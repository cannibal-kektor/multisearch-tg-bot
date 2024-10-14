package lubos.multisearch.processor.bot.commands.helper;

import com.google.common.escape.Escaper;
import lubos.multisearch.processor.entrypoint.ActionMessage;

import java.util.Locale;

import static com.google.common.html.HtmlEscapers.htmlEscaper;

public final class TelegramUtils {

    private static final Escaper ESCAPER = htmlEscaper();

    private TelegramUtils() {
    }

    public static String escape(String text) {
        if (text != null) {
            return ESCAPER.escape(text);
        }
        return null;
    }

    public static String stripTag(String username) {
        String lowerCase = username.toLowerCase();
        return lowerCase.startsWith("@") ? lowerCase.substring(1) : lowerCase;
    }

    public static String username(ActionMessage actionMessage) {
        return actionMessage.user().getUserName().toLowerCase();
    }

    public static Locale userLocale(ActionMessage actionMessage) {
        return Locale.of(actionMessage.user().getLanguageCode());
    }
}
