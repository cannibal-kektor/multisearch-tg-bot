package lubos.multisearch.processor.parser;

import lubos.multisearch.processor.model.DocumentType;
import lubos.multisearch.processor.model.elastic.Chapter;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.escape;

@Component
public class DocxParser implements Parser {

    private static final String DOCX = ".docx";

    @Override
    public boolean isSupported(String documentName) {
        return documentName.endsWith(DOCX);
    }

    @Override
    public List<Chapter> parse(URL documentURL) throws IOException, XmlException {
        try (InputStream inputStream = documentURL.openStream();
             XWPFDocument docx = new XWPFDocument(inputStream)) {
//            docx.getTable().getElementType()
            Map<String, Integer> styleToOutlineMap = getStyleToOutlineMap(docx);
            List<Chapter> chaptersList = new ArrayList<>();

            String currentHeading = "NO TITLE";
            int currentOutlineLevel = 0;
            StringBuilder builder = new StringBuilder();
            Deque<String> currentHeadingPath = new LinkedList<>();
            currentHeadingPath.add("DOCUMENT_START");
            int chapterSerialNumber = 1;

            for (IBodyElement elem : docx.getBodyElements())
                if (elem instanceof XWPFParagraph p) {
                    int newOutlineLvl;

                    if ((newOutlineLvl = getOutlineLvl(styleToOutlineMap, p)) != -1) {

                        chaptersList.add(
                                Chapter.builder()
                                        .title(currentHeading)
                                        .chapterPath(String.join("/", currentHeadingPath))
                                        .serialNumber(chapterSerialNumber++)
                                        .content(escape(builder.toString()))
                                        .type(DocumentType.FILE)
                                        .build());
                        builder.setLength(0);
                        currentHeading = p.getText();

                        if (newOutlineLvl > currentOutlineLevel) {
                            currentHeadingPath.addLast(currentHeading);
                        } else {
                            while (currentHeadingPath.size() > newOutlineLvl) {
                                currentHeadingPath.pollLast();
                            }
                            currentHeadingPath.addLast(currentHeading);
                        }
                        currentOutlineLevel = newOutlineLvl;

                    } else {
                        builder.append(p.getText()).append(System.lineSeparator());
                    }
                } else if (elem instanceof XWPFTable t) {
                    builder.append(t.getText()).append(System.lineSeparator());
                }

            chaptersList.add(
                    Chapter.builder()
                            .title(currentHeading)
                            .chapterPath(String.join("/", currentHeadingPath))
                            .serialNumber(chapterSerialNumber)
                            .content(escape(builder.toString()))
                            .type(DocumentType.FILE)
                            .build());

            return chaptersList;
        }
    }

    private int getOutlineLvl(Map<String, Integer> s, XWPFParagraph p) {
        if (s.containsKey(p.getStyleID())) return s.get(p.getStyleID());

        if (p.getCTPPr().isSetOutlineLvl()) {
            return p.getCTPPr().getOutlineLvl().getVal().intValue();
        }
        return -1;
    }

    private Map<String, Integer> getStyleToOutlineMap(XWPFDocument docx) throws XmlException, IOException {
        return docx.getStyle()
                .getStyleList()
                .stream()
                .filter(style -> style.getPPr() != null && style.getPPr().isSetOutlineLvl())
                .collect(Collectors.toMap(CTStyle::getStyleId, style -> style.getPPr().getOutlineLvl().getVal().intValue()));
    }


}
