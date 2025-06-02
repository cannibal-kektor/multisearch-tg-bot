package lubos.multisearch.processor.bot.commands.impl;


import lubos.multisearch.processor.bot.commands.CommandProcessor;
import lubos.multisearch.processor.bot.commands.PageableCommand;
import lubos.multisearch.processor.entrypoint.CommandActionContext;
import lubos.multisearch.processor.dto.DocumentDTO;
import lubos.multisearch.processor.service.DocumentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;
import java.util.Locale;

import static lubos.multisearch.processor.bot.commands.Command.DOCUMENTS;
import static lubos.multisearch.processor.bot.commands.impl.DocumentsContentsCommand.DOCUMENT_CONTENTS_CALLBACK_FORMAT;
import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.*;


@Component
public class DocumentsCommand extends CommandProcessor implements PageableCommand {

    static final String LIST_DOCUMENTS_PAGE_FORMAT = "doc_page %d";
    private static final String LIST_DOCUMENTS_INFO = "command.documents.info";
    private static final String PLURAL = "command.documents.plural";
    private static final String DELETE_OPTION = "command.documents.delete_button";
    private static final String DOCS_PAGE = "docsPage";

    private final DocumentService documentService;

    public DocumentsCommand(DocumentService documentService) {
        super(DOCUMENTS, LIST_DOCUMENTS_INFO, PUBLIC);
        this.documentService = documentService;
    }

    @Override
    public void process(CommandActionContext context) {
        switch (context.contextId()) {
            case MESSAGE, MENU_CALLBACK -> handle(context, pageConfigs().page());
            case PAGING_CALLBACK -> {
                int pageNum = Integer.parseInt(context.params().get(DOCS_PAGE));
                Pageable pageable = pageConfigs().page().withPage(pageNum);
                handle(context, pageable);
            }
        }
    }

    private void handle(CommandActionContext context, Pageable pageable) {
        Long userId = context.user().getId();
        var page = documentService.listFiles(userId, pageable);
        Locale locale = userLocale(context);
        String result = pageToString(page, locale);
        var keyboard = formKeyboard(page, userId, locale);
        sender.sendDocs(context.chatId(), result, page, keyboard);
    }

    private List<InlineKeyboardRow> formKeyboard(Page<DocumentDTO> page, Long userId, Locale locale) {
        var rows = page.map(doc -> {
                    var contentsButton = keyboard.button(doc.documentName(),
                            DOCUMENT_CONTENTS_CALLBACK_FORMAT.formatted(0, doc.id(), -1, page.getNumber()));
                    var deleteButton = keyboard.button(DELETE_OPTION,
                            DeleteCommand.DOCUMENT_DELETE_CALLBACK_FORMAT.formatted(doc.id(), page.getNumber()),
                            locale);
                    return new InlineKeyboardRow(contentsButton, deleteButton);
                })
                .toList();
        return formPageableKeyboard(page, rows, userId, locale);
    }

    @Override
    public PageConfig pageConfigs() {
        return new PageConfig(LIST_DOCUMENTS_PAGE_FORMAT,
                Pageable.ofSize(2), PLURAL);
    }
}
