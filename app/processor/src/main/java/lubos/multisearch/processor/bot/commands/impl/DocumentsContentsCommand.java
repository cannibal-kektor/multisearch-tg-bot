package lubos.multisearch.processor.bot.commands.impl;

import lubos.multisearch.processor.bot.commands.Command;
import lubos.multisearch.processor.bot.commands.CommandProcessor;
import lubos.multisearch.processor.bot.commands.PageableCommand;
import lubos.multisearch.processor.entrypoint.ActionMessage;
import lubos.multisearch.processor.dto.ChapterCompressedDTO;
import lubos.multisearch.processor.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static lubos.multisearch.processor.bot.commands.impl.ChapterCommand.CHAPTER_PAGE_CALLBACK_FORMAT;
import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.*;


@Component
public class DocumentsContentsCommand extends CommandProcessor implements PageableCommand {

    static final String DOCUMENT_CONTENTS_CALLBACK_FORMAT = "doc_contents %d %s chapter %d docs %d";
    private static final String DOC_CONTENTS_INFO = "command.contents.info";
    private static final String PLURAL = "command.contents.plural";
    private static final String GO_TO_FIRST_CHAPTER = "command.contents.go_to_first_chapter";
    private static final String BACK = "command.contents.back";
    private static final String BACK_TO_DOCS = "command.contents.back_to_docs";
    private static final String RESPONSE_PREFIX_FORMAT = "command.contents.response_prefix";
    private static final String CONTENTS_PAGE = "contentsPage";
    private static final String DOC_ID = "docId";
    private static final String CHAPTERS_PAGE = "chapterPage";
    private static final String DOCS_PAGE = "docsPage";

    private final SearchService searchService;

    public DocumentsContentsCommand(SearchService searchService) {
        super(Command.CONTENTS, DOC_CONTENTS_INFO, PUBLIC);
        this.searchService = searchService;
    }


    @Override
    public void processCommand(ActionMessage actionMessage) {
        switch (actionMessage.contextId()) {
            case MESSAGE, REPLY_MESSAGE, MENU_CALLBACK -> handleMessage(actionMessage, actionMessage.params());
            case PAGING_CALLBACK -> handleCallback(actionMessage, actionMessage.params());
        }
    }


    private void handleMessage(ActionMessage actionMessage, Map<String, String> params) {
        String documentName = params.get(INPUT_ARG);
        var page = searchService.getDocumentContents(documentName, username(actionMessage), pageConfigs().page());
        Locale locale = userLocale(actionMessage);
        String resultStr = pageToString(page, message(RESPONSE_PREFIX_FORMAT, locale, documentName), locale);
        params.put(DOC_ID, page.getContent().getFirst().documentId());
        var keyboard = formKeyboard(page, params, actionMessage.user().getId(), locale);
        sender.send(actionMessage.chatId(), resultStr, keyboard);
    }


    private void handleCallback(ActionMessage actionMessage, Map<String, String> params) {
        int pageNum = Integer.parseInt(params.get(CONTENTS_PAGE));
        String documentId = params.get(DOC_ID);
        var page = searchService.getDocumentContents(documentId, pageConfigs().page().withPage(pageNum));
        Locale locale = userLocale(actionMessage);
        String result = pageToString(page, message(RESPONSE_PREFIX_FORMAT, locale, page.getContent().getFirst().documentName()), locale);
        var keyboard = formKeyboard(page, params, actionMessage.user().getId(), locale);
        sender.send(actionMessage.chatId(), result, keyboard);
    }


    private List<InlineKeyboardRow> formKeyboard(Page<ChapterCompressedDTO> page, Map<String, String> params,
                                                 Long userId, Locale locale) {
        String docId = params.get(DOC_ID);
        int chaptersPage = params.containsKey(CHAPTERS_PAGE) ?
                Integer.parseInt(params.get(CHAPTERS_PAGE)) : -1;
        int docsPage = params.containsKey(DOCS_PAGE) ?
                Integer.parseInt(params.get(DOCS_PAGE)) : -1;

        List<InlineKeyboardRow> rows = new ArrayList<>();
        if (chaptersPage != -1) {
            rows.add(keyboard.singleButtonRow(BACK,
                    CHAPTER_PAGE_CALLBACK_FORMAT.formatted(chaptersPage, docId), locale));
        }
        if (docsPage != -1) {
            rows.add(keyboard.singleButtonRow(BACK_TO_DOCS,
                    DocumentsCommand.LIST_DOCUMENTS_PAGE_FORMAT.formatted(docsPage), locale));
        }
        rows.add(keyboard.singleButtonRow(GO_TO_FIRST_CHAPTER,
                CHAPTER_PAGE_CALLBACK_FORMAT.formatted(0, docId), locale));

        return formPageableKeyboard(page, rows, userId, locale, docId, chaptersPage, docsPage);
    }

    @Override
    public PageConfig pageConfigs() {
        return new PageConfig(DOCUMENT_CONTENTS_CALLBACK_FORMAT,
                PageRequest.of(0, 10, Sort.by("serialNumber").ascending()),
                PLURAL);
    }
}
