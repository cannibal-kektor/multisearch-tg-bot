package lubos.multisearch.processor.model.elastic;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lubos.multisearch.processor.model.DocumentType;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@TypeAlias("chapter")
@Document(indexName = "chapters")
@Setting(settingPath = "elastic/settings.json")
//@Mapping
public class Chapter {

    @Id
    String id;

    //    @NotBlank нужен кастомный валидатор?
    @Field(type = FieldType.Keyword)
    String documentName;

    @Field(type = FieldType.Keyword)
    String documentId;

    //    @NotBlank
    @Field(type = FieldType.Keyword)
    String userName;

    @Field(type = FieldType.Keyword)
    String title;

    @Field(type = FieldType.Keyword)
    String chapterPath;

    @Field(type = FieldType.Short)
    Integer serialNumber;

    @Field(type = FieldType.Text, analyzer = "ru_eng")
//  @Size?????
    String content;

    @Field(type = FieldType.Keyword)
    DocumentType type;

}
