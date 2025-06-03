package lubos.multisearch.processor.bot.commands.impl;

import lubos.multisearch.processor.bot.commands.Command;
import lubos.multisearch.processor.bot.commands.CommandProcessor;
import lubos.multisearch.processor.bot.commands.helper.TelegramUtils;
import lubos.multisearch.processor.entrypoint.CommandActionContext;
import lubos.multisearch.processor.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Locale;


@Component
public class StartCommand extends CommandProcessor {

    private static final String START_INFO = "command.start.info";
    private static final String INFO_DESCRIPTION_FULL = "command.info.bot_description";
    private static final String REQUEST_FOR_REGISTRATION_PENDING = "command.start.registration_request_pending";

    @Value("classpath:animations/start_animation.gif")
    private Resource startAnimation;

    public final UserService userService;

    public StartCommand(UserService userService) {
        super(Command.START, START_INFO, PUBLIC);
        this.userService = userService;
    }

    @Override
    public void process(CommandActionContext context) {
        User user = context.user();
        Long chatId = context.chatId();
        Locale locale = TelegramUtils.userLocale(context);
        sender.sendAnimation(chatId, startAnimation);
        sender.send(chatId, message(INFO_DESCRIPTION_FULL, locale));
        if (!userService.userExists(user.getId())) {
            if (user.getId() == botInfo.creatorId()) {
                userService.createUser(user);
                return;
            }
            userService.createRequestForRegistration(user, chatId);
            sender.send(chatId, message(REQUEST_FOR_REGISTRATION_PENDING, locale));
        }
    }


}
