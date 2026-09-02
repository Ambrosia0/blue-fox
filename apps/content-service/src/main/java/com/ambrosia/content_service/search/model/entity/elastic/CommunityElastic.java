package com.ambrosia.content_service.search.model.entity.elastic;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public record CommunityElastic(
    @Field(name = "communityId", type = FieldType.Keyword)
    Long communityId,

    @Field(name = "isPrivate", type = FieldType.Boolean)
    boolean isPrivate
) {
    public static CommunityElastic create(Long communityId, Boolean isPrivate){
        return new CommunityElastic(communityId, isPrivate);
    }
}
