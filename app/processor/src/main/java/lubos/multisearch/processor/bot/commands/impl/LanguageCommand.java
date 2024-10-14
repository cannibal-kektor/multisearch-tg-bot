package lubos.multisearch.processor.bot.commands.impl;

import lubos.multisearch.processor.bot.commands.Command;
import lubos.multisearch.processor.bot.commands.CommandProcessor;
import lubos.multisearch.processor.bot.commands.helper.TelegramUtils;
import lubos.multisearch.processor.entrypoint.ActionMessage;
import lubos.multisearch.processor.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;
import java.util.Locale;
import java.util.Map;


@Component
public class LanguageCommand extends CommandProcessor {

    static final String LANGUAGE_CHANGE_CALLBACK_FORMAT = "lang %s";
    private static final String LANGUAGE_CALLBACK = "LANGUAGE_CALLBACK";
    private static final String LANGUAGE_INFO = "command.language.info";
    private static final String CHOOSE_LANGUAGE_STR = "command.language.choose_str";
    private static final String LANGUAGE_CHANGE_SUCCESS = "command.language.success";
    private static final String LANG = "lang";
    private static final String RUS = "ru";
    private static final String ENG = "en";

    private final UserService userService;

    public LanguageCommand(UserService userService) {
        super(Command.LANGUAGE, LANGUAGE_INFO, PUBLIC);
        this.userService = userService;
    }

    @Override
    public void processCommand(ActionMessage actionMessage) {
        switch (actionMessage.contextId()) {
            case MESSAGE -> handleMessage(actionMessage);
            case LANGUAGE_CALLBACK -> handleCallback(actionMessage, actionMessage.params());
        }
    }

    private void handleMessage(ActionMessage actionMessage) {
        Locale locale = TelegramUtils.userLocale(actionMessage);
        var langChooseKeyboard = formKeyboard();
        sender.send(actionMessage.chatId(), message(CHOOSE_LANGUAGE_STR, locale), langChooseKeyboard);
    }

    private void handleCallback(ActionMessage actionMessage, Map<String, String> parameters) {
        String lang = parameters.get(LANG);
        Long userId = actionMessage.user().getId();
        userService.setUserLanguage(userId, lang);
        Locale newLocale = Locale.of(lang);
        sender.send(actionMessage.chatId(), message(LANGUAGE_CHANGE_SUCCESS, newLocale),
                keyboard.commandsKeyboard(userId, newLocale));
    }

    private List<InlineKeyboardRow> formKeyboard() {
        var englishButton = keyboard.button("English", LANGUAGE_CHANGE_CALLBACK_FORMAT.formatted(ENG));
        var rusButton = keyboard.button("Русский", LANGUAGE_CHANGE_CALLBACK_FORMAT.formatted(RUS));
        return List.of(new InlineKeyboardRow(englishButton, rusButton));
    }
}
