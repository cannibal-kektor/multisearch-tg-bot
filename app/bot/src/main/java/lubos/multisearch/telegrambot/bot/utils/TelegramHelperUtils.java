package lubos.multisearch.telegrambot.bot.utils;

import com.google.common.escape.Escaper;
import org.telegram.telegrambots.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;
import java.util.function.Consumer;

import static com.google.common.html.HtmlEscapers.htmlEscaper;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getUser;

public final class TelegramHelperUtils {

    public static Consumer<MessageContext> BOT_TYPING =
            ctx -> {
                var action = SendChatAction.builder()
                        .action(ActionType.TYPING.toString())
                        .chatId(ctx.chatId())
                        .build();
                ctx.bot().getSilent().execute(action);
            };

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
