package lubos.multisearch.processor.bot.commands;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lubos.multisearch.processor.conf.BotInfo;
import lubos.multisearch.processor.bot.commands.helper.TelegramKeyboard;
import lubos.multisearch.processor.bot.commands.helper.TelegramSender;
import lubos.multisearch.processor.entrypoint.ActionMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static lombok.AccessLevel.PROTECTED;


@Getter
@FieldDefaults(level = PROTECTED)
public abstract class CommandProcessor {

    public static final int PUBLIC = 1;
    public static final int ADMIN = 2;
    public static final int CREATOR = 3;

    protected static final String INPUT_ARG = "inputArg";
    protected static final String USERNAME = "username";

    protected static final String MESSAGE = "MESSAGE";
    protected static final String REPLY_MESSAGE = "REPLY_MESSAGE";
    protected static final String MENU_CALLBACK = "MENU_CALLBACK";
    protected static final String PAGING_CALLBACK = "PAGING_CALLBACK";

    @Autowired
    BotInfo botInfo;
    @Autowired
    TelegramSender sender;
    @Autowired
    TelegramKeyboard keyboard;
    @Autowired
    MessageSource messageSource;

    final Command command;
    final String commandInfo;
    final int privacy;


    public CommandProcessor(Command command, String commandInfo, int privacy) {
        this.command = command;
        this.commandInfo = commandInfo;
        this.privacy = privacy;
    }


    public abstract void processCommand(ActionMessage actionMessage);

    public String message(String messageCode, Locale locale) {
        return messageSource.getMessage(messageCode, null, locale);
    }

    public String message(String messageCode, Locale locale, Object... arguments) {
        return messageSource.getMessage(messageCode, arguments, locale);
    }


}
