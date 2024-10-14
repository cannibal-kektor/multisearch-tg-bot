package lubos.multisearch.processor.service;

import lubos.multisearch.processor.dto.ChapterCompressedDTO;
import lubos.multisearch.processor.dto.ChapterDTO;
import lubos.multisearch.processor.dto.SearchDTO;
import lubos.multisearch.processor.model.elastic.Chapter;
import lubos.multisearch.processor.model.mongo.TelegramUser;
import lubos.multisearch.processor.repository.elastic.ChapterRepository;
import lubos.multisearch.processor.repository.mongo.UserRepository;
import lubos.multisearch.processor.exception.DocumentProcessingException;
import lubos.multisearch.processor.exception.DocumentTypeNotSupported;
import lubos.multisearch.processor.exception.NotFoundException;
import lubos.multisearch.processor.mapping.SearchMapping;
import lubos.multisearch.processor.parser.Parser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.List;

@Service
public class SearchService {

    final ChapterRepository chapterRepository;
    final UserRepository userRepository;
    final List<Parser> parsers;
    final SearchMapping mapper;

    public SearchService(ChapterRepository chapterRepository, UserRepository userRepository, List<Parser> parsers, SearchMapping mapper) {
        this.chapterRepository = chapterRepository;
        this.userRepository = userRepository;
        this.parsers = parsers;
        this.mapper = mapper;
    }

    public Page<ChapterDTO> getChapterById(String chapterId, String username) {
        return chapterRepository.findById(chapterId)
                .map(mapper::toDTO)
                .map(dto -> switch (dto.type()) {
                            case FILE -> {
                                var total = chapterRepository.countByUserNameAndDocumentName(username, dto.documentName());
                                yield new PageImpl<>(List.of(dto), PageRequest.of(dto.serialNumber() - 1, 1), total);
                            }
                            case HTML -> new PageImpl<>(List.of(dto), PageRequest.of(0, 1), 1);
                        }
                )
                .orElseThrow(() -> new NotFoundException(chapterId));
    }

    public Page<ChapterDTO> getChapterBySerialNumberInDocument(String fileId, String username, Pageable pageable) {
        return chapterRepository.findByUserNameAndDocumentId(username, fileId, pageable)
                .map(SearchHit::getContent)
                .map(mapper::toDTO);
    }


    public Page<ChapterCompressedDTO> getDocumentContents(String documentName, String username, Pageable pageable) {
        var page = chapterRepository.findByUserNameAndDocumentName(username, documentName, pageable)
                .map(SearchHit::getContent)
                .map(mapper::toCompressedDTO);

        if (page.isEmpty()) {
            throw new NotFoundException(documentName);
        }
        return page;
    }

    public Page<ChapterCompressedDTO> getDocumentContents(String documentId, Pageable pageable) {
        var page = chapterRepository.findByDocumentId(documentId, pageable)
                .map(SearchHit::getContent)
                .map(mapper::toCompressedDTO);
        if (page.isEmpty()) {
            throw new NotFoundException(documentId);
        }
        return page;
    }

    //    @Transactional(label = "mongo:writeConcern=ACKNOWLEDGED|W1|W2|W3|UNACKNOWLEDGED|JOURNALED|MAJORITY")
    @Transactional
    public Page<SearchDTO> search(String search, String username, Pageable pageable) {
        var searchHits = chapterRepository.findBySearchStringAndUserName(search, username, pageable);
        var content = searchHits
                .map(mapper::toSearchDTO)
                .toList();
        userRepository.findAndSetLastSearchByUsername(username, search, (short) pageable.getPageNumber());
        return new PageImpl<>(content, pageable, searchHits.getTotalElements());
    }


    @Transactional(readOnly = true)
    public TelegramUser.LastSearch getUserLastSearch(Long id) {
        return userRepository.findLastSearchByTelegramId(id);
    }


    List<Chapter> parseInputSource(URL docUrl, String documentName) {
        try {
            for (Parser parser : parsers) {
                if (parser.isSupported(documentName)) {
                    return parser.parse(docUrl);
                }
            }
        } catch (Exception e) {
            throw new DocumentProcessingException(documentName, e);
        }
        throw new DocumentTypeNotSupported(documentName);
    }
}
