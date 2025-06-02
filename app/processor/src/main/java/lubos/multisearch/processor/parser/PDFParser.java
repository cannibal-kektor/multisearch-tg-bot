package lubos.multisearch.processor.parser;


import lubos.multisearch.processor.model.DocumentType;
import lubos.multisearch.processor.model.elastic.Chapter;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Pattern;

import static java.lang.System.lineSeparator;
import static lubos.multisearch.processor.bot.commands.helper.TelegramUtils.escape;

@Component
public class PDFParser implements Parser {

    private static final String PDF = ".pdf";
    private static final String PAGE_NUM = "Page №";
    private static final Pattern removePageHeader = Pattern.compile("\\R{4,}(?<match>[^\\r\\n]{0,70})\\R");
    private static final Pattern pageNumHeaderGuess = Pattern.compile("^(?=.{1,29}$)(\\d+.*|.*\\d+)$");
    private static final Pattern largeGaps = Pattern.compile("\\R{3,}");

    @Override
    public boolean isSupported(String documentName) {
        return documentName.endsWith(PDF);
    }

    public List<Chapter> parse(URL documentURL) throws IOException {

        try (InputStream inputStream = documentURL.openStream();
             PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {
            PDFTextStripper stripper = getPdfTextStripper();
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

    @NotNull
    private static PDFTextStripper getPdfTextStripper() {
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setSortByPosition(true);
        stripper.setIndentThreshold(2.0f); // Отступ
        stripper.setDropThreshold(2.5f); // Вертикальный пробел
        stripper.setLineSeparator(" ");
        stripper.setParagraphStart("\n");
        stripper.setParagraphEnd("\n");
        stripper.setArticleStart("\n");
        stripper.setPageStart("\n");
        return stripper;
    }


    private List<Chapter> parseDocumentPageByPage(PDDocument document, PDFTextStripper stripper) throws IOException {
        List<Chapter> results = new ArrayList<>();
        for (int i = 1; i <= document.getNumberOfPages(); i++) {
            stripper.setStartPage(i);
            stripper.setEndPage(i);
            String content = stripper.getText(document);

            Chapter chapter = Chapter.builder()
                    .type(DocumentType.FILE)
                    .title(PAGE_NUM + i)
                    .chapterPath(PAGE_NUM + i)
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
                    nextTitle = nextChapter.title;
                }
                String chapterContent = stripper.getText(document);
                chapterContent = preciseContentBoundary( chapterInfo.title, nextChapter != null ? nextTitle : null,
                        chapterInfo.chapterPath, chapterContent);
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

    private String preciseContentBoundary(String title, String nextTitle, String titlePath, String chapterContent) {
        int startIndex = chapterContent.indexOf(lineSeparator() +title);
        int endIndex = -1;
        if (nextTitle != null) {
            endIndex = chapterContent.indexOf(lineSeparator() + nextTitle, startIndex != -1 ? startIndex : 0);
        }
        var borderedContent = chapterContent.substring(startIndex != -1 ? startIndex : 0,
                endIndex != -1 ? endIndex : !chapterContent.isEmpty() ? chapterContent.length() - 1 : 0);
        return removeMeta(borderedContent, titlePath);
    }

    private String removeMeta(String content, String titlePath) {
        var matcher = removePageHeader.matcher(content);
        while (matcher.find()) {
            boolean isHeader = false;
            String match = matcher.group("match");
            for( String title : titlePath.split(" / ") ){
                if (match.contains(title)){
                    isHeader = true;
                    break;
                }
            }
            if (pageNumHeaderGuess.matcher(match.strip()).find()){
                isHeader = true;
            }
            if (isHeader)
                content = content.replace(matcher.group(), "");
        }
        return largeGaps.matcher(content).replaceAll("");
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
