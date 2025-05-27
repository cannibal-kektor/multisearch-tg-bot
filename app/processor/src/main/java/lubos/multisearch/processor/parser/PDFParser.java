package lubos.multisearch.processor.parser;


import lubos.multisearch.processor.model.DocumentType;
import lubos.multisearch.processor.model.elastic.Chapter;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static java.lang.System.lineSeparator;
import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.escape;

@Component
public class PDFParser implements Parser {

    private static final String PDF = ".pdf";

    @Override
    public boolean isSupported(String documentName) {
        return documentName.endsWith(PDF);
    }

    public List<Chapter> parse(URL documentURL) throws IOException {

//        File file = new File("D:\\test\\Nigel_Poulton_-_Docker_Deep_Dive_Zero_to_Docker_in_a_single_book_2020.pdf");
//        File file = new File("D:\\test\\Getting_Started_with_RabbitMQ_and_CloudAMQP.pdf");
//        File file = new File("D:\\test\\Алфёров А.П., Зубов А.Ю., Кузьмин А.С., Черемушкин А.В. Основы криптографии.pdf");

        try (InputStream inputStream = documentURL.openStream();
             PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {
//        try (PDDocument document = Loader.loadPDF(new File(filename))) {

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            stripper.setAddMoreFormatting(true);

            List<Chapter> results;

            PDDocumentOutline root = document.getDocumentCatalog().getDocumentOutline();

            if (root != null) {
                results = parseDocumentWithOutline(document, stripper, root);
            } else {
                results = parseDocumentPageByPage(document, stripper);
            }

            return results;
        }
    }


    private List<Chapter> parseDocumentPageByPage(PDDocument document, PDFTextStripper stripper) throws IOException {
        List<Chapter> results = new ArrayList<>();
        for (int i = 1; i <= document.getNumberOfPages(); i++) {
            stripper.setStartPage(i);
            stripper.setEndPage(i);
            String content = stripper.getText(document);

            Chapter chapter = Chapter.builder()
                    .type(DocumentType.FILE)
                    .title("Page №" + i)
                    .chapterPath("Page №" + i)
                    .serialNumber(i)
                    .content(escape(content))
                    .build();

            results.add(chapter);
        }

        return results;
    }

    private List<Chapter> parseDocumentWithOutline(PDDocument document, PDFTextStripper stripper, PDDocumentOutline outlineRoot) throws IOException {
        Queue<OutlineElement> contents = new LinkedList<>();
        PDOutlineItem item = outlineRoot.getFirstChild();
        buildDocumentContents(item, null, 1, contents);

        List<Chapter> results = new ArrayList<>();

        OutlineElement chapterInfo = contents.poll();
        if (chapterInfo != null) {
            OutlineElement nextChapter;
            String nextTitle = null;

            for (nextChapter = contents.poll(); ; ) {
                stripper.setStartBookmark(chapterInfo.outline);
                if (nextChapter != null) {
                    stripper.setEndBookmark(nextChapter.outline);
                    nextTitle = lineSeparator() + nextChapter.title + lineSeparator();
                }

                String chapterContent = stripper.getText(document);
                chapterContent = preciseContentBoundary(lineSeparator() + chapterInfo.title + lineSeparator(),
                        nextChapter != null ? nextTitle : null, chapterContent);

                Chapter chapter = Chapter.builder()
                        .type(DocumentType.FILE)
                        .title(chapterInfo.title)
                        .chapterPath(chapterInfo.chapterPath)
                        .serialNumber(chapterInfo.serialNum)
                        .content(escape(chapterContent))
                        .build();

                results.add(chapter);

                if (nextChapter == null) {
                    break;
                }
                chapterInfo = nextChapter;
                nextChapter = contents.poll();
            }

        }
        return results;

    }

    private String preciseContentBoundary(String title, String nextTitle, String chapterContent) {
        int startIndex = chapterContent.indexOf(title);
        int endIndex = -1;
        if (nextTitle != null) {
            endIndex = chapterContent.indexOf(nextTitle, startIndex != -1 ? startIndex : 0);
        }
        return chapterContent.substring(startIndex != -1 ? startIndex : 0,
                endIndex != -1 ? endIndex : !chapterContent.isEmpty() ? chapterContent.length() - 1 : 0);
    }


    private int buildDocumentContents(PDOutlineItem item, String parentTitle, Integer serialNum, Queue<OutlineElement> contents) {
        while (item != null) {
            String titlePath = parentTitle == null ? item.getTitle() : parentTitle + " / " + item.getTitle();
            contents.add(new OutlineElement(item.getTitle(), titlePath, serialNum, item));
            serialNum = buildDocumentContents(item.getFirstChild(), titlePath, ++serialNum, contents);
            item = item.getNextSibling();
        }
        return serialNum;
    }


    private record OutlineElement(String title, String chapterPath, Integer serialNum, PDOutlineItem outline) {
    }


}
