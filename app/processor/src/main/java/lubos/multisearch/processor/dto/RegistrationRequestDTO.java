package lubos.multisearch.processor.dto;

import org.springframework.context.MessageSource;

import java.util.Locale;

import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.*;


public record RegistrationRequestDTO(
        Long telegramId,
        String username
) implements Localizable {

    private static final String REGISTRATION_REQUEST_DTO_FORMAT = "dto.registration_request";

    @Override
    public String toLocalizedString(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(REGISTRATION_REQUEST_DTO_FORMAT,
                new Object[]{escape(username), telegramId}, locale);
    }
}
