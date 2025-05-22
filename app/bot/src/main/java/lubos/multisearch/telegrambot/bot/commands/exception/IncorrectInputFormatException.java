package lubos.multisearch.telegrambot.bot.commands.exception;

import lombok.Setter;
import lubos.multisearch.telegrambot.bot.utils.TelegramHelperUtils;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class IncorrectInputFormatException extends RuntimeException {

    public static final String INCORRECT_INPUT_ARG_FORMAT = "exception.incorrect_input_arguments_format";

    String inputText;
    @Setter
    String command;

    public IncorrectInputFormatException(String inputText) {
        this.inputText = inputText;
    }
    public IncorrectInputFormatException(String inputText, String command) {
        this.inputText = inputText;
        this.command = command;
    }

    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(INCORRECT_INPUT_ARG_FORMAT, new Object[]{TelegramHelperUtils.escape(inputText), command}, locale);
    }
}
