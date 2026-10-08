package com.ambrosia.community_service.community.application.query.model;

import org.springframework.data.elasticsearch.core.SearchHit;

import com.ambrosia.community_service.community.domain.entity.elastic.ElasticCommunity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public record CommunityScoredPreview(
    CommunityPreview communityPreview,

    @JsonInclude(value = Include.NON_NULL)
    Float score
) {
    public static CommunityScoredPreview from(SearchHit<ElasticCommunity> elasticCommunity){
        return new CommunityScoredPreview(
            new CommunityPreview(
                Long.parseLong(elasticCommunity.getContent().getId()),
                elasticCommunity.getContent().getSlug(),
                elasticCommunity.getContent().getDisplayedName(),
                elasticCommunity.getContent().getFollowCount(),
                elasticCommunity.getContent().getAvatarId(),
                elasticCommunity.getContent().getTags(),
                elasticCommunity.getContent().getCreatedAt()
            ), 
            elasticCommunity.getScore()
        );
    }
}