package lubos.multisearch.processor.mapping;


import lubos.multisearch.processor.dto.ChapterCompressedDTO;
import lubos.multisearch.processor.dto.ChapterDTO;
import lubos.multisearch.processor.dto.SearchDTO;
import lubos.multisearch.processor.model.elastic.Chapter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.elasticsearch.core.SearchHit;

import java.util.List;

@Mapper(config = MapConfig.class)
public interface SearchMapping {

    ChapterDTO toDTO(Chapter chapter);

    ChapterCompressedDTO toCompressedDTO(Chapter chapter);

    @Mapping(target = ".", source = "content")
    @Mapping(target = "highlights", source = "searchHit")
    SearchDTO toSearchDTO(SearchHit<Chapter> searchHit);

    default List<String> toHighlights(SearchHit<Chapter> searchHit) {
        return searchHit.getHighlightField("content");
    }

}
