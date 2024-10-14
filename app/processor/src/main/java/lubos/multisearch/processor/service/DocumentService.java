package lubos.multisearch.processor.service;

import lubos.multisearch.processor.dto.DocumentDTO;
import lubos.multisearch.processor.model.elastic.Chapter;
import lubos.multisearch.processor.model.mongo.SearchDocument;
import lubos.multisearch.processor.repository.elastic.ChapterRepository;
import lubos.multisearch.processor.repository.mongo.DocumentRepository;
import lubos.multisearch.processor.exception.NotFoundException;
import lubos.multisearch.processor.mapping.DocumentMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.User;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static lubos.multisearch.processor.model.DocumentType.FILE;
import static lubos.multisearch.processor.model.DocumentType.HTML;
import static lubos.multisearch.processor.model.mongo.TelegramUser.onlyId;

@Service
public class DocumentService {

    ChapterRepository chapterRepository;
    DocumentRepository documentRepository;
    SearchService searchService;
    DocumentMapper mapper;

    public DocumentService(ChapterRepository chapterRepository, DocumentRepository documentRepository, SearchService searchService, DocumentMapper mapper) {
        this.chapterRepository = chapterRepository;
        this.documentRepository = documentRepository;
        this.searchService = searchService;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<DocumentDTO> listFiles(Long userId, Pageable pageable) {
        return documentRepository.findByUser(onlyId(userId), pageable)
                .map(mapper::toDTO);
    }

    @Transactional
    public String uploadHTML(User user, String fileUrl) throws MalformedURLException {
        URL docUrl;
        docUrl = new URL(fileUrl);
        Chapter chapter = searchService.parseInputSource(docUrl, fileUrl).getFirst();
        SearchDocument document = SearchDocument.builder()
                .documentType(HTML)
                .documentName(fileUrl)
                .htmlTitle(chapter.getTitle())
                .size((long) chapter.getContent().length())
                .user(onlyId(user.getId()))
                .build();

        String docId = documentRepository.insert(document).getId();
        chapter.setDocumentId(docId);
        chapter.setDocumentName(fileUrl);
        chapter.setUserName(user.getUserName().toLowerCase());
        chapterRepository.save(chapter);
        return docId;
    }

    @Transactional
    public String uploadFile(String fileName, String fileId, Long fileSize, User user, String link) throws MalformedURLException {
        URL docURL = new URL(link);

        var document = SearchDocument.builder()
                .documentType(FILE)
                .documentName(fileName)
                .telegramFileId(fileId)
                .size(fileSize)
                .user(onlyId(user.getId()))
                .build();

        List<Chapter> chapters = searchService.parseInputSource(docURL, fileName);
        String docId = documentRepository.insert(document).getId();

        for (var p : chapters) {
            p.setDocumentName(fileName);
            p.setDocumentId(docId);
            p.setUserName(user.getUserName().toLowerCase());
        }
        chapterRepository.saveAll(chapters);
        return docId;
    }


    @Transactional
    public void deleteDocument(String fileName, User user) {
        Integer num = documentRepository.deleteByDocumentNameAndUser(fileName, onlyId(user.getId()));
        if (num == 0) {
            throw new NotFoundException(fileName);
        }
        chapterRepository.deleteByDocumentNameAndUserName(fileName, user.getUserName().toLowerCase());
    }


    @Transactional
    public void deleteDocument(String documentId) {
        documentRepository.deleteById(documentId);
        chapterRepository.deleteByDocumentId(documentId);
    }

    @Transactional
    public boolean purgeUser(Long userTelegramId, String username) {
        int num = documentRepository.deleteAllByUser(onlyId(userTelegramId));
        chapterRepository.deleteAllByUserName(username);
        return num > 0;
    }
}
