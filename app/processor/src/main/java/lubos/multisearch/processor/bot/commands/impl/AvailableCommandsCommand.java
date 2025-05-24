package lubos.multisearch.processor.bot.commands.impl;

import lubos.multisearch.processor.entrypoint.CommandActionContext;
import lubos.multisearch.processor.bot.commands.CommandProcessor;
import lubos.multisearch.processor.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static lubos.multisearch.processor.bot.commands.Command.COMMANDS;
import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.userLocale;

@Component
public class AvailableCommandsCommand extends CommandProcessor {

    private static final String COMMANDS_INFO = "command.commands.info";
    private static final String AVAILABLE_COMMANDS_PREFIX = "command.commands.list_prefix";
    private final UserService userService;
    private String allCommandsResponseRu;
    private String allCommandsResponseEn;
    private String adminCommandsResponseRu;
    private String adminCommandsResponseEn;
    private String userCommandsResponseRu;
    private String userCommandsResponseEn;

    public AvailableCommandsCommand(UserService userService) {
        super(COMMANDS, COMMANDS_INFO, PUBLIC);
        this.userService = userService;
    }

    @Autowired
    public void prebuildAnswers(List<CommandProcessor> commands) {
        allCommandsResponseRu = buildResponse(commands, CREATOR, Locale.of("ru"));
        allCommandsResponseEn = buildResponse(commands, CREATOR, Locale.ENGLISH);
        adminCommandsResponseRu = buildResponse(commands, ADMIN, Locale.of("ru"));
        adminCommandsResponseEn = buildResponse(commands, ADMIN, Locale.ENGLISH);
        userCommandsResponseRu = buildResponse(commands, PUBLIC, Locale.of("ru"));
        userCommandsResponseEn = buildResponse(commands, PUBLIC, Locale.ENGLISH);
    }


    @Override
    public void process(CommandActionContext context) {
        Locale locale = userLocale(context);
        Long userId = context.user().getId();
        int privacy = userService.getUserPrivacy(userId);
        boolean isRussian = locale.getLanguage().equals("ru");
        String result = switch (privacy) {
            case PUBLIC -> isRussian ? userCommandsResponseRu : userCommandsResponseEn;
            case ADMIN -> isRussian ? adminCommandsResponseRu : adminCommandsResponseEn;
            case CREATOR -> isRussian ? allCommandsResponseRu : allCommandsResponseEn;
            default -> throw new IllegalArgumentException();
        };
        sender.send(context.chatId(), result, keyboard.commandsKeyboard(userId, locale));
    }

    private String buildResponse(List<CommandProcessor> commands, int privacy, Locale locale) {
        return commands
                .stream()
                .filter(command -> command.getPrivacy() <= privacy)
                .sorted(comparing(command -> command.getCommand().name()))
                .map(command -> format("<b>/%s</b> --> %s", command.getCommand().toString().toLowerCase()
                        , message(command.getCommandInfo(), locale)))
                .collect(joining("\n", message(AVAILABLE_COMMANDS_PREFIX, locale), ""));
    }

}

