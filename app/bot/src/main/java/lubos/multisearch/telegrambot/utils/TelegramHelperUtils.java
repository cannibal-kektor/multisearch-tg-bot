package lubos.multisearch.telegrambot.utils;

import com.google.common.escape.Escaper;
import org.telegram.telegrambots.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

import static com.google.common.html.HtmlEscapers.htmlEscaper;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getUser;

public final class TelegramHelperUtils {

    public static final Escaper ESCAPER = htmlEscaper();

    private TelegramHelperUtils() {
    }

    public static String escape(String text) {
        if (text != null) {
            return ESCAPER.escape(text);
        }
        return null;
    }

    public static Locale userLocale(MessageContext ctx) {
        return Locale.of(ctx.user().getLanguageCode());
    }

    public static Locale userLocale(Update upd) {
        return Locale.of(getUser(upd).getLanguageCode());
    }

}
