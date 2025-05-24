package lubos.multisearch.processor.bot.commands.impl;

import lubos.multisearch.processor.bot.commands.CommandProcessor;
import lubos.multisearch.processor.bot.commands.PageableCommand;
import lubos.multisearch.processor.entrypoint.CommandActionContext;
import lubos.multisearch.processor.dto.RegistrationRequestDTO;
import lubos.multisearch.processor.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;
import java.util.Locale;

import static lubos.multisearch.processor.bot.commands.Command.REGISTRATION_REQUESTS;
import static lubos.multisearch.processor.bot.commands.impl.RegisterCommand.REGISTER_USER_CALLBACK_FORMAT;
import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.*;


@Component
public class RegistrationRequestsCommand extends CommandProcessor implements PageableCommand {

    static final String REGISTRATION_REQUEST_CALLBACK_FORMAT = "reg_page %d";
    private static final String REGISTRATION_REQUESTS_INFO = "command.registration_requests.info";
    private static final String PLURAL = "command.registration_requests.plural";
    private static final String REGISTER_BUTTON = "command.registration_requests.register_button";
    private static final String REGISTRATION_PAGE = "regPage";

    private final UserService userService;

    public RegistrationRequestsCommand(UserService userService) {
        super(REGISTRATION_REQUESTS, REGISTRATION_REQUESTS_INFO, ADMIN);
        this.userService = userService;
    }


    @Override
    public void process(CommandActionContext context) {
        switch (context.contextId()) {
            case MESSAGE, MENU_CALLBACK -> handle(context, pageConfigs().page());
            case PAGING_CALLBACK -> {
                int pageNum = Integer.parseInt(context.params().get(REGISTRATION_PAGE));
                Pageable pageable = pageConfigs().page().withPage(pageNum);
                handle(context, pageable);
            }
        }
    }


    private void handle(CommandActionContext context, Pageable pageable) {
        var page = userService.listUserRegistrationRequests(pageable);
        Locale locale = userLocale(context);
        String result = pageToString(page, locale);
        var keyboard = formKeyboard(page, context.user().getId(), locale);
        sender.send(context.chatId(), result, keyboard);
    }


    private List<InlineKeyboardRow> formKeyboard(Page<RegistrationRequestDTO> page,
                                                 Long userId, Locale locale) {
        var rows = page.map(registrationDTO -> keyboard.singleButtonRow(REGISTER_BUTTON,
                        REGISTER_USER_CALLBACK_FORMAT.formatted(registrationDTO.username(), page.getNumber()),
                        locale, registrationDTO.username()))
                .toList();
        return formPageableKeyboard(page, rows, userId, locale);
    }


    @Override
    public PageConfig pageConfigs() {
        return new PageConfig(REGISTRATION_REQUEST_CALLBACK_FORMAT,
                Pageable.ofSize(1), PLURAL);
    }
}
