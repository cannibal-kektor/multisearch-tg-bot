package lubos.multisearch.processor.bot.commands.impl;

import lubos.multisearch.processor.bot.commands.Command;
import lubos.multisearch.processor.bot.commands.CommandProcessor;
import lubos.multisearch.processor.entrypoint.ActionMessage;
import lubos.multisearch.processor.service.DocumentService;
import lubos.multisearch.processor.exception.DocumentNameExistsException;
import lubos.multisearch.processor.exception.DocumentTypeNotSupported;
import lubos.multisearch.processor.exception.FileTooBigException;
import lubos.multisearch.processor.exception.DocumentLinkFailedException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.*;

@Component
public class UploadCommand extends CommandProcessor {

    private static final String UPLOAD_DOCUMENT_INFO = "command.upload.info";
    private static final String DOCUMENT_UPLOADED = "command.upload.success";
    private static final String LIST_CONTENTS_BUTTON = "command.upload.list_contents_button";
    private static final String FILE_NAME = "FILE_NAME";
    private static final String FILE_ID = "FILE_ID";
    private static final String HTTP_LINK = "HTTP_LINK";
    private static final String FILE_SIZE = "FILE_SIZE";

    private static final Predicate<String> IS_SUPPORTED_FILE = documentName -> documentName.endsWith(".docx") || documentName.endsWith(".pdf");
    private static final Predicate<String> IS_MORE_THAN_20_MB = fileSize -> Long.parseLong(fileSize) > 20_000_000;
    private static final Predicate<String> IS_HTTP =
            Pattern.compile("^https?://(.*)").asMatchPredicate();

    private final DocumentService documentService;

    public UploadCommand(DocumentService documentService) {
        super(Command.UPLOAD, UPLOAD_DOCUMENT_INFO, PUBLIC);
        this.documentService = documentService;
    }

    @Override
    public void processCommand(ActionMessage actionMessage) {
        boolean isFile = actionMessage.params().get(FILE_ID) != null;
        try {
            String documentId = isFile ? handleFile(actionMessage, actionMessage.params()) :
                    handleHTML(actionMessage, actionMessage.params());
            Locale locale = userLocale(actionMessage);
            sender.send(actionMessage.chatId(), message(DOCUMENT_UPLOADED, locale),
                    formKeyboard(documentId, actionMessage.user().getId(), locale));
        } catch (MalformedURLException e) {
            throw new DocumentLinkFailedException(e);
        }
    }

    private String handleFile(ActionMessage actionMessage, Map<String, String> params) throws MalformedURLException {
        String fileName = params.get(FILE_NAME);

        if (!IS_SUPPORTED_FILE.test(fileName)) {
            throw new DocumentTypeNotSupported(fileName);
        }
        if (IS_MORE_THAN_20_MB.test(params.get(FILE_SIZE))) {
            throw new FileTooBigException(fileName);
        }
        User user = actionMessage.user();
        String fileId = params.get(FILE_ID);
        File file = sender.fetchFileDownloadInfo(fileId, fileName);
        String fileLink = file.getFileUrl(botInfo.token());

        try {
            return documentService.uploadFile(fileName, fileId, file.getFileSize(), user, fileLink);
        } catch (DuplicateKeyException e) {
            throw new DocumentNameExistsException(fileName);
        }
    }

    private String handleHTML(ActionMessage actionMessage, Map<String, String> params) throws MalformedURLException {
//        String httpLink = params.get(HTML_LINK); ///pattern?
        String httpLink = params.get(HTTP_LINK);
        if (!IS_HTTP.test(httpLink)) {
            throw new DocumentTypeNotSupported(httpLink);
        }
        try {
            return documentService.uploadHTML(actionMessage.user(), httpLink);
        } catch (DuplicateKeyException e) {
            throw new DocumentNameExistsException(httpLink);
        }
    }

    private List<InlineKeyboardRow> formKeyboard(String documentId, Long userId, Locale locale) {
        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();
        var listDocumentContentsButton = keyboard.singleButtonRow(LIST_CONTENTS_BUTTON,
                DocumentsContentsCommand.DOCUMENT_CONTENTS_CALLBACK_FORMAT.formatted(0, documentId, -1, -1),
                locale);
        keyboardRows.add(listDocumentContentsButton);
        keyboardRows.addAll(keyboard.commandsKeyboard(userId, locale));
        return keyboardRows;
    }
}
