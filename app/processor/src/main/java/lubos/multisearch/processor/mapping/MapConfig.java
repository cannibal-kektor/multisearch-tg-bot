package lubos.multisearch.processor.mapping;

import org.mapstruct.*;
import org.mapstruct.control.NoComplexMapping;

@MapperConfig(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
//        unmappedSourcePolicy = ReportingPolicy.IGNORE,
//        builder = @Builder,
        nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
//        nullValueMapMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
//        nullValuePropertyMappingStrategy =  NullValuePropertyMappingStrategy.SET_TO_NULL,
//        collectionMappingStrategy = CollectionMappingStrategy.ACCESSOR_ONLY,
//        suppressTimestampInGenerated = false,
        typeConversionPolicy = ReportingPolicy.ERROR,
//        mappingInheritanceStrategy = MappingInheritanceStrategy.EXPLICIT,
        mappingControl = NoComplexMapping.class
//        disableSubMappingMethodsGeneration = true
)
public interface MapConfig {
}

