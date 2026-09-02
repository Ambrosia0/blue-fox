package com.ambrosia.profile_service.user.model.entity.elastic;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Persistable;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(indexName = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setting(settingPath = "elastic-settings.json")
public class ElasticUser implements Persistable<UUID>{
    @Id
    private UUID id;

    @Field(name = "username", type = FieldType.Text, analyzer = "edge_ngram_analyzer")
    private String username;

    @Field(name = "firstName", type = FieldType.Text)
    private String firstName;

    @Field(name = "lastName", type = FieldType.Text)
    private String lastName;

    @Field(name = "avatarId", type = FieldType.Keyword)
    private String avatarId;

    @Builder.Default
    @Field(name = "followCount", type = FieldType.Long)
    private long followCount = 0L;

    @Version
    private Long version;

    @Transient
    @Builder.Default
    @JsonProperty("isNew")
    private boolean isNew = false;
}
