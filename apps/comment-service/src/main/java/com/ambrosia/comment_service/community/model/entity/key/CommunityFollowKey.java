package com.ambrosia.comment_service.community.model.entity.key;

import java.util.UUID;

import org.springframework.data.relational.core.mapping.Column;

public record CommunityFollowKey(
    @Column(value = "user_id") UUID userId,
    @Column(value = "community_id") Long communityId
) {
    public static CommunityFollowKey create(UUID userId, long communityId){
        return new CommunityFollowKey(userId, communityId);
    }
}
