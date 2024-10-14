package lubos.multisearch.processor.bot.commands;

import lubos.multisearch.processor.bot.commands.helper.TelegramKeyboard;
import lubos.multisearch.processor.dto.Localizable;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

public interface PageableCommand {

    String PAGE_TITLE_FORMAT = "pageable.title.format";
    String NO_ELEMENTS_FORMAT = "pageable.no_elements.format";
    String PREVIOUS = "pageable.prev";
    String NEXT = "pageable.next";

    PageConfig pageConfigs();

    default <T extends Localizable> String pageToString(Page<T> page, Locale locale) {
        return pageToString(page, "", locale);
    }

    default <T extends Localizable> String pageToString(Page<T> page, String prefix, Locale locale) {
        String plural = message(pageConfigs().itemNames, locale);
        if (page.isEmpty()) {
            return message(NO_ELEMENTS_FORMAT, locale, plural);
        }
        int pageOffset = (int) page.getPageable().getOffset();
        String completePrefix = prefix +
                message(PAGE_TITLE_FORMAT, locale, plural,
                        page.getTotalElements(), plural,
                        pageOffset + 1, pageOffset + page.getNumberOfElements());
        int[] num = {pageOffset};
        return page.stream()
                .map(elemet -> elemet.toLocalizedString(getMessageSource(), locale))
                .map(str -> "<b><u>" + (++num[0]) + "</u>.</b> " + str)
                .collect(joining("\n", completePrefix, "\n"));
    }

    default List<InlineKeyboardRow> formPageableKeyboard(Page<?> page, List<InlineKeyboardRow> additionalRows,
                                                         Long userId, Locale locale, Object... arguments) {
        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();
        var pageKeyboardRow = getPageScrollKeyboardRow(page, locale, arguments);
        keyboardRows.add(pageKeyboardRow);
        keyboardRows.addAll(additionalRows);
        keyboardRows.addAll(getKeyboard().commandsKeyboard(userId, locale));
        return keyboardRows;
    }

    default InlineKeyboardRow getPageScrollKeyboardRow(Page<?> page, Locale locale, Object... arguments) {
        InlineKeyboardRow row = new InlineKeyboardRow();
        int pageNumber = page.getNumber();
        Object[] formatArgs;

        if (arguments.length != 0) {
            formatArgs = new Object[arguments.length + 1];
            System.arraycopy(arguments, 0, formatArgs, 1, arguments.length);
        } else {
            formatArgs = new Object[]{pageNumber};
        }

        if (page.hasPrevious()) {
            formatArgs[0] = pageNumber - 1;
            row.add(getKeyboard().button(PREVIOUS, format(pageConfigs().callbackFormat, formatArgs),
                    locale));
        }

        if (page.hasNext()) {
            formatArgs[0] = pageNumber + 1;
            row.add(getKeyboard().button(NEXT, format(pageConfigs().callbackFormat, formatArgs),
                    locale));
        }
        return row;

    }

    MessageSource getMessageSource();

    TelegramKeyboard getKeyboard();

    String message(String messageCode, Locale locale, Object... arguments);

    String message(String messageCode, Locale locale);

    record PageConfig(String callbackFormat,
                      Pageable page,
                      String itemNames) {
    }

}
