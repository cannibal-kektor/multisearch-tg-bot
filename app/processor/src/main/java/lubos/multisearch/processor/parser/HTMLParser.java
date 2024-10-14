package lubos.multisearch.processor.parser;

import lubos.multisearch.processor.model.DocumentType;
import lubos.multisearch.processor.model.elastic.Chapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.escape;

@Component
public class HTMLParser implements Parser {

    private static final Predicate<String> HTTP_PATTERN =
            Pattern.compile("^https?://(.*)").asMatchPredicate();

    @Override
    public boolean isSupported(String documentName) {
        return HTTP_PATTERN.test(documentName);
    }

    @Override
    public List<Chapter> parse(URL documentURL) throws IOException {
        Document document = Jsoup.parse(documentURL, 5000);
        return List.of(Chapter.builder()
                .title(document.title())
                .content(escape(document.text()))
                .serialNumber(1)
                .type(DocumentType.HTML)
                .build());
    }
}
