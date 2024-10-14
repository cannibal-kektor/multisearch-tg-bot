package lubos.multisearch.processor.repository.elastic;

import lubos.multisearch.processor.model.elastic.Chapter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ChapterRepository extends ElasticsearchRepository<Chapter, String> {

    @SourceFilters(excludes = "content")
    @Highlight(fields = @HighlightField(name = "content"
            , parameters = @HighlightParameters(boundaryMaxScan = 50,
//                    encoder = "html",
            fragmentSize = 500, preTags = "<em><b>", postTags = "</b></em>")))
    @Query("""
            {
              "bool": {
                "must": [
                  {
                    "match": {
                      "content": "?0"
                    }
                  },
                  {
                    "term": {
                      "userName": "?1"
                    }
                  }]}
              }
            """)
    SearchPage<Chapter> findBySearchStringAndUserName(String search, String userName, Pageable page);

    SearchPage<Chapter> findByUserNameAndDocumentId(String userName, String documentId, Pageable pageable);

    @SourceFilters(excludes = "content")
    SearchPage<Chapter> findByUserNameAndDocumentName(String username, String documentName, Pageable pageable);

    @SourceFilters(excludes = "content")
    SearchPage<Chapter> findByDocumentId(String documentId, Pageable pageable);

    Integer countByUserNameAndDocumentName(String userName, String documentName);

    long deleteByDocumentNameAndUserName(String documentName, String username);

    long deleteByDocumentId(String documentId);

    long deleteAllByUserName(String username);

}
