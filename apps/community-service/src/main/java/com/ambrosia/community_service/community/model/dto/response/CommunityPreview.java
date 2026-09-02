package com.ambrosia.community_service.community.model.dto.response;

import java.time.Instant;

import org.springframework.data.elasticsearch.core.SearchHit;

import com.ambrosia.community_service.community.model.entity.elastic.ElasticCommunity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public record CommunityPreview(
    long id,
    String slug,
    String displayedName,

    long followCount,

    String avatarId,

    String[] tags,

    @JsonInclude(value = Include.NON_NULL)
    Float score,

    Instant createdAt
) {
    public static CommunityPreview from(SearchHit<ElasticCommunity> elasticCommunity){
        return new CommunityPreview(
            Long.parseLong(elasticCommunity.getContent().getId()),
            elasticCommunity.getContent().getSlug(),
            elasticCommunity.getContent().getDisplayedName(),
            elasticCommunity.getContent().getFollowCount(),
            elasticCommunity.getContent().getAvatarId(),
            elasticCommunity.getContent().getTags(),
            elasticCommunity.getScore(),
            elasticCommunity.getContent().getCreatedAt()
        );
    }
}
