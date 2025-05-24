package lubos.multisearch.processor.bot.commands.impl;

import lubos.multisearch.processor.bot.commands.CommandProcessor;
import lubos.multisearch.processor.entrypoint.CommandActionContext;
import lubos.multisearch.processor.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static lubos.multisearch.processor.bot.commands.Command.REGISTER;
import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.*;


@Component
public class RegisterCommand extends CommandProcessor {

    static final String REGISTER_USER_CALLBACK_FORMAT = "reg %s regPage %d";
    private static final String REGISTER_INFO = "command.register.info";
    private static final String USER_REGISTERED = "command.register.success";
    private static final String USER_REGISTRATION_REQUEST_APPROVED = "command.register.user_notification";
    private static final String BACK_TO_REGISTRATIONS_REQUESTS_BUTTON = "command.register.back_to_registration_requests_button";
    private static final String REG_PAGE = "regPage";

    private final UserService userService;

    public RegisterCommand(UserService userService) {
        super(REGISTER, REGISTER_INFO, ADMIN);
        this.userService = userService;
    }

    @Override
    public void process(CommandActionContext context) {
        String username = stripTag(context.params().get(USERNAME));
        Long pendingUserChatId = userService.registerUser(username);
        Locale locale = userLocale(context);
        sender.send(pendingUserChatId, message(USER_REGISTRATION_REQUEST_APPROVED, locale));
        var keyboard = formKeyboard(context.params(), context.user().getId(), locale);
        sender.send(context.chatId(), message(USER_REGISTERED, locale), keyboard);
    }


    private List<InlineKeyboardRow> formKeyboard(Map<String, String> params,
                                                 Long userId, Locale locale) {
        List<InlineKeyboardRow> rows = new ArrayList<>();
        int registrationPageNum = params.containsKey(REG_PAGE) ?
                Integer.parseInt(params.get(REG_PAGE)) : -1;

        if (registrationPageNum != -1) {
            rows.add(keyboard.singleButtonRow(BACK_TO_REGISTRATIONS_REQUESTS_BUTTON,
                    RegistrationRequestsCommand.REGISTRATION_REQUEST_CALLBACK_FORMAT.formatted(registrationPageNum),
                    locale));
        }
        rows.addAll(keyboard.commandsKeyboard(userId, locale));
        return rows;
    }
}
