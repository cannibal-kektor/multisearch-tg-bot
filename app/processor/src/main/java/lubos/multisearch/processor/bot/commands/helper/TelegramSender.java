package lubos.multisearch.processor.bot.commands.helper;

import lubos.multisearch.processor.exception.DocumentLinkFailedException;
import org.jsoup.nodes.Entities;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

@Component
public class TelegramSender {

    private static final int MAX_MESSAGE_SIZE = 4096;
    //    static final int MAX_RAW_MESSAGE_SIZE = 2048;
    private static final Pattern REMOVE_HTML_TAGS_PATTERN = Pattern.compile("<b>|</b>|<blockquote>|</blockquote>|<code>|</code>|<i>|</i>|<u>|</u>|<em>|</em>");
    private final TelegramClient telegramClient;

    public TelegramSender(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    public File fetchFileDownloadInfo(String fileId, String fileName) {
        GetFile getFile = new GetFile(fileId);
        return execute(getFile)
                .orElseThrow(() -> new DocumentLinkFailedException(fileId, fileName));
    }

    public void send(Long chatId, String message) {
        send(chatId, message, null);
    }

    public void send(Long chatId, String message, List<InlineKeyboardRow> keyboardRows) {
        if (message.length() < MAX_MESSAGE_SIZE)
            execute(buildHTMLMsg(chatId, message, keyboardRows));
        else {
            var messages = splitMessage(message, MAX_MESSAGE_SIZE);
            var iterator = messages.iterator();
            while (iterator.hasNext()) {
                String msgStr = iterator.next();
                boolean isLast = !iterator.hasNext();
                var tgMessage = isLast ? buildHTMLMsg(chatId, msgStr, keyboardRows)
                        : buildHTMLMsg(chatId, msgStr, null);
                Optional<Message> resp = execute(tgMessage);
                if (resp.isEmpty()) {
                    //split again because raw message size is bigger than size of html parsed message
//                            splitMessage(msg.getText(), MAX_RAW_MESSAGE_SIZE)
//                                    .forEach(rawMsg -> bot.getSilent().execute(buildRawMsg(chatId, rawMsg, keyboard)));
                    String rawMsg = removeTags(msgStr);
                    rawMsg = Entities.unescape(rawMsg);
//                            Unescape raw text https://howtodoinjava.com/java/string/unescape-html-to-string/
                    execute(buildRawMsg(chatId, rawMsg, isLast ? keyboardRows : null));
                }
            }
        }
    }

    private SendMessage buildHTMLMsg(Long chatId, String message, List<InlineKeyboardRow> keyboardRows) {
        return buildMsg(chatId, message, keyboardRows, ParseMode.HTML);
    }

    private SendMessage buildRawMsg(Long chatId, String message, List<InlineKeyboardRow> keyboardRows) {
        return buildMsg(chatId, message, keyboardRows, null);
    }


    private SendMessage buildMsg(Long chatId, String message, List<InlineKeyboardRow> keyboardRows, String parseMode) {
        ReplyKeyboard keyboardMarkup = null;
        if (keyboardRows != null)
            keyboardMarkup = new InlineKeyboardMarkup(keyboardRows);
        return SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .parseMode(parseMode)
                .replyMarkup(keyboardMarkup)
                .build();
    }

    private String removeTags(String message) {
        return REMOVE_HTML_TAGS_PATTERN.matcher(message).replaceAll("");
    }


    private List<String> splitMessage(String message, int maxSize) {
        int messageSize = message.length();
        List<String> splittedMessage = new ArrayList<>();
        int step = maxSize;
        int i = 0;
        while (i < messageSize) {
            if (step > messageSize - i) {
                step = messageSize - i;
            }
            splittedMessage.add(message.substring(i, i + step));
            i += step;
        }
        return splittedMessage;
    }

    private <T extends Serializable, Method extends BotApiMethod<T>> Optional<T> execute(Method method) {
        try {
            return ofNullable(telegramClient.execute(method));
        } catch (TelegramApiException e) {
//            log.error("Could not execute bot API method", e);
            return empty();
        }
    }


}
