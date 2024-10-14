package lubos.multisearch.processor.exception;

import org.springframework.context.MessageSource;

import java.net.MalformedURLException;
import java.util.Locale;

import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.escape;


public class DocumentLinkFailedException extends ApplicationException {

    private static final String TELEGRAM_BOT_API_FILE_DOWNLOAD_FAILED = "exception.telegram_bot_api_download_link_fail";
    private static final String INCORRECT_LINK_TO_DOCUMENT = "exception.link_malformed";

    private String tgFileId;
    private String filename;


    public DocumentLinkFailedException(String tgFileId, String filename) {
        this.tgFileId = tgFileId;
        this.filename = filename;
    }

    public DocumentLinkFailedException(MalformedURLException cause) {
        super(cause);
    }

    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return tgFileId != null ? messageSource.getMessage(TELEGRAM_BOT_API_FILE_DOWNLOAD_FAILED, new Object[]{filename, tgFileId}, locale)
                : messageSource.getMessage(INCORRECT_LINK_TO_DOCUMENT, new Object[]{escape(getCause().getMessage())}, locale);
    }

}
