package lubos.multisearch.processor.bot.commands.impl;

import lubos.multisearch.processor.bot.commands.CommandProcessor;
import lubos.multisearch.processor.entrypoint.ActionMessage;
import lubos.multisearch.processor.service.DocumentService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static lubos.multisearch.processor.bot.commands.Command.DELETE;
import static lubos.multisearch.processor.bot.commands.impl.DocumentsCommand.LIST_DOCUMENTS_PAGE_FORMAT;
import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.*;


@Component
public class DeleteCommand extends CommandProcessor {

    static final String DOCUMENT_DELETE_CALLBACK_FORMAT = "doc_delete %s back_docs %d";
    private static final String DOC_DELETE_CALLBACK = "DOC_DELETE_CALLBACK";
    private static final String DELETE_DOCUMENT_INFO = "command.delete.info";
    private static final String DOCUMENT_DELETED = "command.delete.success";
    private static final String BACK_TO_DOCUMENTS = "command.delete.back_to_documents";
    private static final String DOC_ID = "docId";
    private static final String BACK_DOCS = "backDocs";

    private final DocumentService documentService;

    public DeleteCommand(DocumentService documentService) {
        super(DELETE, DELETE_DOCUMENT_INFO, PUBLIC);
        this.documentService = documentService;
    }


    @Override
    public void processCommand(ActionMessage actionMessage) {
        switch (actionMessage.contextId()) {
            case MESSAGE, REPLY_MESSAGE, MENU_CALLBACK -> handleMessage(actionMessage, actionMessage.params());
            case DOC_DELETE_CALLBACK -> handleCallback(actionMessage, actionMessage.params());
        }
    }

    private void handleMessage(ActionMessage actionMessage, Map<String, String> params) {
        String documentName = params.get(INPUT_ARG);
        documentService.deleteDocument(documentName, actionMessage.user());
        Locale locale = userLocale(actionMessage);
        var keyboard = formKeyboard(params, actionMessage.user().getId(), locale);
        sender.send(actionMessage.chatId(), message(DOCUMENT_DELETED, locale), keyboard);
    }

    private void handleCallback(ActionMessage actionMessage, Map<String, String> params) {
        String documentId = params.get(DOC_ID);
        documentService.deleteDocument(documentId);
        Locale locale = userLocale(actionMessage);
        var keyboard = formKeyboard(params, actionMessage.user().getId(), locale);
        sender.send(actionMessage.chatId(), message(DOCUMENT_DELETED, locale), keyboard);
    }


    private List<InlineKeyboardRow> formKeyboard(Map<String, String> params, Long userId, Locale locale) {
        var rows = new ArrayList<InlineKeyboardRow>();
        if (params.containsKey(BACK_DOCS)) {
            int docsPage = Integer.parseInt(params.get(BACK_DOCS));
            rows.add(keyboard.singleButtonRow(BACK_TO_DOCUMENTS,
                    LIST_DOCUMENTS_PAGE_FORMAT.formatted(docsPage), locale));
        }
        rows.addAll(keyboard.commandsKeyboard(userId, locale));
        return rows;
    }
}
