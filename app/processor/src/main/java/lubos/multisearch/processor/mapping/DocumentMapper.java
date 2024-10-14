package lubos.multisearch.processor.mapping;


import lubos.multisearch.processor.dto.DocumentDTO;
import lubos.multisearch.processor.model.mongo.SearchDocument;
import org.mapstruct.Mapper;

@Mapper(config = MapConfig.class)
public interface DocumentMapper {
    DocumentDTO toDTO(SearchDocument document);
}
