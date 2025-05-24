package lubos.multisearch.processor.bot.commands.impl;

import lubos.multisearch.processor.bot.commands.Command;
import lubos.multisearch.processor.bot.commands.CommandProcessor;
import lubos.multisearch.processor.bot.commands.PageableCommand;
import lubos.multisearch.processor.entrypoint.CommandActionContext;
import lubos.multisearch.processor.dto.ChapterDTO;
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

import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.*;


@Component
public class ChapterCommand extends CommandProcessor implements PageableCommand {

    static final String CHAPTER_PAGE_CALLBACK_FORMAT = "chapter_num %d %s";
    static final String EXPAND_CHAPTER_CALLBACK_FORMAT = "chapter %s";
    private static final String CHAPTER_EXPAND_CALLBACK = "CHAPTER_EXPAND_CALLBACK";
    private static final String CHAPTER_INFO = "command.chapter.info";
    private static final String PLURAL = "command.chapter.plural";
    private static final String LIST_CONTENTS_BUTTON = "command.chapter.list_contents_button";
    private static final String LAST_SEARCH_BUTTON = "command.chapter.last_search_button";
    private static final String CHAPTER_PAGE = "chapterPage";
    private static final String DOC_ID = "docId";
    private static final String CHAPTER_ID = "chapterId";

    private final SearchService searchService;

    public ChapterCommand(SearchService searchService) {
        super(Command.CHAPTER, CHAPTER_INFO, PUBLIC);
        this.searchService = searchService;
    }

    @Override
    public void process(CommandActionContext context) {
        switch (context.contextId()) {
            case MESSAGE, REPLY_MESSAGE, CHAPTER_EXPAND_CALLBACK -> handle(context, context.params());
            case PAGING_CALLBACK -> handlePageableCallback(context, context.params());
        }
    }

    private void handle(CommandActionContext context, Map<String, String> params) {
        String chapterId = params.get(CHAPTER_ID);
        Page<ChapterDTO> page = searchService.getChapterById(chapterId, username(context));
        String documentId = page.getContent().getFirst().documentId();
        Locale locale = userLocale(context);
        String result = pageToString(page, locale);
        var keyboardRows = formKeyboard(page, documentId, context.user().getId(), locale);
        sender.send(context.chatId(), result, keyboardRows);
    }

    private void handlePageableCallback(CommandActionContext context, Map<String, String> params) {
        int serialNum = Integer.parseInt(params.get(CHAPTER_PAGE));
        var pageable = pageConfigs().page().withPage(serialNum);
        String documentId = params.get(DOC_ID);
        Page<ChapterDTO> page = searchService.getChapterBySerialNumberInDocument(documentId, username(context), pageable);
        Locale locale = userLocale(context);
        String result = pageToString(page, locale);
        var keyboardRows = formKeyboard(page, documentId, context.user().getId(), locale);
        sender.send(context.chatId(), result, keyboardRows);
    }

    private List<InlineKeyboardRow> formKeyboard(Page<ChapterDTO> page, String documentId, Long userId, Locale locale) {
        List<InlineKeyboardRow> rows = new ArrayList<>();

        var lastSearch = searchService.getUserLastSearch(userId);
        if (lastSearch != null && lastSearch.searchStr() != null) {
            rows.add(keyboard.singleButtonRow(LAST_SEARCH_BUTTON,
                    SearchCommand.SEARCH_CALLBACK_FORMAT.formatted(lastSearch.pageNum()),
                    locale));
        }
        rows.add(keyboard.singleButtonRow(LIST_CONTENTS_BUTTON,
                DocumentsContentsCommand.DOCUMENT_CONTENTS_CALLBACK_FORMAT.formatted(0, documentId, page.getNumber(), -1),
                locale));

        return formPageableKeyboard(page, rows, userId, locale, documentId);
    }

    @Override
    public PageConfig pageConfigs() {
        return new PageConfig(CHAPTER_PAGE_CALLBACK_FORMAT,
                PageRequest.of(0, 1, Sort.by("serialNumber").ascending()), PLURAL);
    }
}
