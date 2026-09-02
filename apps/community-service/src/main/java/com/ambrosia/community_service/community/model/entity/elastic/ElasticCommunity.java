package com.ambrosia.community_service.community.model.entity.elastic;

import java.time.Instant;

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

import com.ambrosia.community_service.community.model.entity.Community;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(indexName = "community", createIndex = true)
@Setting(settingPath = "elastic-settings.json")
public class ElasticCommunity implements Persistable<String>{
    @Id
    private String id;

    @MultiField(
        mainField = @Field(name = "displayedName", type = FieldType.Text, analyzer = "edge_ngram_analyzer"),
        otherFields = {
            @InnerField(suffix = "raw", type = FieldType.Keyword)
        }
    )
    private String displayedName;

    @MultiField(
        mainField = @Field(name = "slug", type = FieldType.Text, analyzer = "edge_ngram_analyzer"),
        otherFields = {
            @InnerField(suffix = "raw", type = FieldType.Keyword)
        }
    )
    private String slug;

    @MultiField(
        mainField = @Field(name = "tags", type = FieldType.Text),
        otherFields = {
            @InnerField(suffix = "raw", type = FieldType.Keyword)
        }
    )
    private String[] tags;

    @Field(name = "avatarId", type = FieldType.Keyword)
    private String avatarId;

    @Field(name = "followCount", type = FieldType.Long)
    private long followCount;

    @Field(name = "createdAt", type = FieldType.Date)
    private Instant createdAt;

    @Version
    private Long version;

    @Transient
    @Builder.Default
    @JsonProperty("isNew")
    private boolean isNew = true;

    public static ElasticCommunity from(Community community){
        return new ElasticCommunity(
            community.getId().toString(),
            community.getDisplayedName(),
            community.getSlug(), 
            community.getTags() != null?
                community.getTags().toArray(String[]::new):
                null,
            community.getAvatarId(),
            community.getFollowCount(),
            community.getCreatedAt(),
            community.getVersion(),
            true
        );
    }
}
