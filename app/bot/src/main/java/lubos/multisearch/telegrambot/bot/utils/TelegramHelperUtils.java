package lubos.multisearch.telegrambot.bot.utils;

import com.google.common.escape.Escaper;
import org.telegram.telegrambots.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.telegrambots.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;
import java.util.function.Consumer;

import static com.google.common.html.HtmlEscapers.htmlEscaper;
import static org.telegram.telegrambots.abilitybots.api.objects.Flag.MESSAGE;
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

    public static void sendHTML(MessageContext ctx, String message) {
        ctx.bot().getSilent().execute(
                SendMessage.builder()
                        .chatId(ctx.chatId())
                        .text(message)
                        .parseMode(ParseMode.HTML)
                        .build());
    }

    public static void send(MessageContext ctx, String message) {
        ctx.bot().getSilent().send(message, ctx.chatId());
    }


}
