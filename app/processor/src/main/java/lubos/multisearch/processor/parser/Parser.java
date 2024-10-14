package lubos.multisearch.processor.parser;

import lubos.multisearch.processor.model.elastic.Chapter;

import java.net.URL;
import java.util.List;

public interface Parser {

    boolean isSupported(String documentName);

    List<Chapter> parse(URL documentURL) throws Exception;

}
