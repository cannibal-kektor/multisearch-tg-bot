package lubos.multisearch.processor.bot.commands.helper;

import lubos.multisearch.processor.dto.DocumentDTO;
import lubos.multisearch.processor.exception.ApplicationException;
import lubos.multisearch.processor.exception.DocumentLinkFailedException;
import lubos.multisearch.processor.logging.LogHelper;
import org.jsoup.nodes.Entities;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;
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

    final LogHelper logHelper;
    final TelegramClient telegramClient;

    public TelegramSender(TelegramClient telegramClient, LogHelper logHelper) {
        this.telegramClient = telegramClient;
        this.logHelper = logHelper;
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
            var messages = splitMessage(message);
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


    private List<String> splitMessage(String message) {
        int messageSize = message.length();
        List<String> splittedMessage = new ArrayList<>();
        int step = MAX_MESSAGE_SIZE;
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

    public void sendDocs(Long chatId, String result, Page<DocumentDTO> page, List<InlineKeyboardRow> keyboard) {
        page.getContent()
                .stream()
                .filter(DocumentDTO::isFile)
                .map(dto ->
                        SendDocument.builder()
                                .chatId(chatId)
                                .document(new InputFile(dto.telegramFileId()))
                                .build())
                .forEach(this::executePartial);
        send(chatId, result, keyboard);
    }

    public void sendAnimation(Long chatId, Resource animationResource) {
        try {
            var file = new InputFile(animationResource.getInputStream(), "start_animation.gif");
            executePartial(SendAnimation.builder()
                    .chatId(chatId)
                    .animation(file)
                    .build());
        } catch (IOException e) {
            throw new ApplicationException(e.getMessage(), e);
        }
    }

    private <T extends Serializable, Method extends BotApiMethod<T>> Optional<T> execute(Method method) {
        try {
            return ofNullable(telegramClient.execute(method));
        } catch (TelegramApiException e) {
            logHelper.logFailedRequestToTelegram(method, e);
            return empty();
        }
    }

    private void executePartial(PartialBotApiMethod<Message> method) {
        try {
            switch (method) {
                case SendDocument sendDocument -> telegramClient.execute(sendDocument);
                case SendAnimation sendAnimation -> telegramClient.execute(sendAnimation);
                default -> throw new ApplicationException("Unsupported method");
            }
        } catch (TelegramApiException e) {
            switch (method) {
                case SendDocument sendDocument -> logHelper.logFailSendingTelegramFile(sendDocument, e);
                case SendAnimation sendAnimation -> logHelper.logFailSendingAnimation(sendAnimation, e);
                default -> throw new ApplicationException("Unsupported method");
            }
        }
    }
}
