package com.ambrosia.content_service.search.model.entity.elastic;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Persistable;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "posts")
@Setting(settingPath = "elastic-settings.json")
public class PostElastic implements Persistable<String>{
    @Id
    private String esid;

    @Field(name = "id", type = FieldType.Keyword)
    private Long id;

    @Field(name = "authorId", type = FieldType.Keyword)
    private String authorId;

    @MultiField(
        mainField = @Field(name = "title", type = FieldType.Text, analyzer = "title_ngram"),
        otherFields = {
            @InnerField(suffix = "raw", type = FieldType.Keyword)
        }
    )
    private String title;

    @Field(name = "content", type = FieldType.Text)
    private String content;

    @MultiField(
        mainField = @Field(name = "tags", type = FieldType.Text),
        otherFields = {
            @InnerField(suffix = "raw", type = FieldType.Keyword)
        }
    )
    private List<String> tags;

    @Field(name = "community", type = FieldType.Object)
    private CommunityElastic community;

    @Field(name = "publishedAt", type = FieldType.Date)
    private Instant publishedAt;

    @Field(name = "likeCount", type = FieldType.Integer)
    private int likeCount;

    @Field(name = "visible", type = FieldType.Boolean)
    private boolean visible;

    @Version
    private Long version;

    @Transient
    @Builder.Default
    @JsonProperty("isNew")
    private boolean isNew = true;

    @Override
    public @Nullable String getId() {
        return esid;
    }
}
