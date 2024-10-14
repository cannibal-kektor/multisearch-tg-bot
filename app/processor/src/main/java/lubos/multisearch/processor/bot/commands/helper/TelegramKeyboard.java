package lubos.multisearch.processor.bot.commands.helper;

import jakarta.annotation.PostConstruct;
import lubos.multisearch.processor.service.UserService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static lubos.multisearch.processor.bot.commands.Command.*;


@Component
public class TelegramKeyboard {

    static final String MENU_CALLBACK_FORMAT = "menu %s";
    private static final String MENU_COMMANDS_LIST_COMMANDS = "keyboard.menu.commands.list_commands";
    private static final String MENU_COMMANDS_SEARCH = "keyboard.menu.commands.search";
    private static final String MENU_COMMANDS_DOCUMENTS = "keyboard.menu.commands.documents";
    private static final String MENU_COMMANDS_UPLOAD = "keyboard.menu.commands.upload";
    private static final String MENU_COMMANDS_DELETE = "keyboard.menu.commands.delete";
    private static final String MENU_COMMANDS_CONTENTS = "keyboard.menu.commands.doc_contents";
    private static final String MENU_COMMANDS_USERS = "keyboard.menu.commands.users";
    private static final String MENU_COMMANDS_REGISTRATIONS_REQUESTS = "keyboard.menu.commands.reg.requests";
    //TODO Stats
    //    public static final String MENU_COMMANDS_STATS = "keyboard.menu.commands.stats";
    private final MessageSource messageSource;
    private final UserService userService;

    private List<InlineKeyboardRow> ruKeyboardUser;
    private List<InlineKeyboardRow> ruKeyboardAdmin;
    private List<InlineKeyboardRow> enKeyboardUser;
    private List<InlineKeyboardRow> enKeyboardAdmin;

    public TelegramKeyboard(MessageSource messageSource, UserService userService) {
        this.messageSource = messageSource;
        this.userService = userService;
    }

    @PostConstruct
    void buildMenuKeyboards() {
        ruKeyboardUser = buildUserCommandsKeyboard(Locale.of("ru"));
        enKeyboardUser = buildUserCommandsKeyboard(Locale.ENGLISH);
        ruKeyboardAdmin = buildAdminCommandsKeyboard(Locale.of("ru"));
        enKeyboardAdmin = buildAdminCommandsKeyboard(Locale.ENGLISH);
    }

    public InlineKeyboardButton button(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }

    public InlineKeyboardButton button(String text, String callbackData,
                                       Locale locale, Object... arguments) {
        return InlineKeyboardButton.builder()
                .text(messageSource.getMessage(text, arguments, locale))
                .callbackData(callbackData)
                .build();
    }

    public InlineKeyboardRow singleButtonRow(String text, String callbackData) {
        return new InlineKeyboardRow(button(text, callbackData));
    }

    public InlineKeyboardRow singleButtonRow(String messageCode, String callbackData,
                                             Locale locale, Object... arguments) {
        return new InlineKeyboardRow(button(messageCode, callbackData, locale, arguments));
    }


    public List<InlineKeyboardRow> commandsKeyboard(Long telegramId, Locale locale) {
        return switch (locale.getLanguage()) {
            case "ru" -> userService.getUserPrivacy(telegramId) > 1 ? ruKeyboardAdmin : ruKeyboardUser;
            case "en" -> userService.getUserPrivacy(telegramId) > 1 ? enKeyboardAdmin : enKeyboardUser;
            default -> enKeyboardUser;
        };
    }

    private List<InlineKeyboardRow> buildUserCommandsKeyboard(Locale locale) {
        var firstRow = new InlineKeyboardRow(
                button(MENU_COMMANDS_LIST_COMMANDS, MENU_CALLBACK_FORMAT.formatted(COMMANDS.name().toLowerCase()), locale),
                button(MENU_COMMANDS_SEARCH, MENU_CALLBACK_FORMAT.formatted(SEARCH.name().toLowerCase()), locale),
                button(MENU_COMMANDS_DOCUMENTS, MENU_CALLBACK_FORMAT.formatted(DOCUMENTS.name().toLowerCase()), locale));
        var secondRow = new InlineKeyboardRow(
                button(MENU_COMMANDS_UPLOAD, MENU_CALLBACK_FORMAT.formatted(UPLOAD.name().toLowerCase()), locale),
                button(MENU_COMMANDS_DELETE, MENU_CALLBACK_FORMAT.formatted(DELETE.name().toLowerCase()), locale),
                button(MENU_COMMANDS_CONTENTS, MENU_CALLBACK_FORMAT.formatted(CONTENTS.name().toLowerCase()), locale));
        return List.of(firstRow, secondRow);
    }

    private List<InlineKeyboardRow> buildAdminCommandsKeyboard(Locale locale) {
        var lastRow = new InlineKeyboardRow(
                button(MENU_COMMANDS_USERS, MENU_CALLBACK_FORMAT.formatted(USERS.name().toLowerCase()), locale),
                button(MENU_COMMANDS_REGISTRATIONS_REQUESTS, MENU_CALLBACK_FORMAT.formatted(REGISTRATION_REQUESTS.name().toLowerCase()), locale));
        var adminKeyboardRows = new ArrayList<>(buildUserCommandsKeyboard(locale));
        adminKeyboardRows.add(lastRow);
        return adminKeyboardRows;
    }

}
