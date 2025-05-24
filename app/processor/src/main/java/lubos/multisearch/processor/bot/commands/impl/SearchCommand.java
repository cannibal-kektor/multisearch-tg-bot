package lubos.multisearch.processor.bot.commands.impl;

import lubos.multisearch.processor.bot.commands.CommandProcessor;
import lubos.multisearch.processor.bot.commands.PageableCommand;
import lubos.multisearch.processor.entrypoint.CommandActionContext;
import lubos.multisearch.processor.dto.SearchDTO;
import lubos.multisearch.processor.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static lubos.multisearch.processor.bot.commands.Command.SEARCH;
import static lubos.multisearch.processor.bot.commands.impl.ChapterCommand.EXPAND_CHAPTER_CALLBACK_FORMAT;
import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.*;


@Component
public class SearchCommand extends CommandProcessor implements PageableCommand {

    static final String SEARCH_CALLBACK_FORMAT = "search %d";
    private static final String SEARCH_INFO = "command.search.info";
    private static final String PLURAL = "command.search.plural";
    private static final String SEARCH_PAGE = "searchPage";
    private static final String SEARCH_STR = "searchStr";

    private final SearchService searchService;

    public SearchCommand(SearchService searchService) {
        super(SEARCH, SEARCH_INFO, PUBLIC);
        this.searchService = searchService;
    }

    @Override
    public void process(CommandActionContext context) {
        switch (context.contextId()) {
            case MESSAGE, REPLY_MESSAGE, MENU_CALLBACK -> handleMessage(context, context.params());
            case PAGING_CALLBACK -> handlePagingCallback(context, context.params());
        }
    }

    private void handleMessage(CommandActionContext context, Map<String, String> parameters) {
        String searchStr = parameters.get(SEARCH_STR);
        Pageable pageable = pageConfigs().page();
        process(context, searchStr, pageable);
    }

    private void handlePagingCallback(CommandActionContext context, Map<String, String> parameters) {
        int pageNum = Integer.parseInt(parameters.get(SEARCH_PAGE));
        Pageable pageable = pageConfigs().page().withPage(pageNum);
        String searchStr = searchService.getUserLastSearch(context.user().getId()).searchStr();
        process(context, searchStr, pageable);
    }


    private void process(CommandActionContext context, String searchStr, Pageable pageable) {
        var page = searchService.search(searchStr, username(context), pageable);
        Locale locale = userLocale(context);
        String result = pageToString(page, locale);
        var keyboardRows = formKeyboard(page, context.user().getId(), locale);
        sender.send(context.chatId(), result, keyboardRows);
    }

    private List<InlineKeyboardRow> formKeyboard(Page<SearchDTO> page,
                                                 Long userId, Locale locale) {
        var rows = page
                .map(searchDTO -> keyboard.singleButtonRow(searchDTO.title(),
                        EXPAND_CHAPTER_CALLBACK_FORMAT.formatted(searchDTO.id())))
                .toList();
        return formPageableKeyboard(page, rows, userId, locale);
    }


    @Override
    public PageConfig pageConfigs() {
        return new PageConfig(SEARCH_CALLBACK_FORMAT,
                Pageable.ofSize(1), PLURAL);
    }
}
