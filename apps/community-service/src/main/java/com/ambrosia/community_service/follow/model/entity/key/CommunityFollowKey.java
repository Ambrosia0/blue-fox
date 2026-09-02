package com.ambrosia.community_service.follow.model.entity.key;

import java.util.UUID;

import org.springframework.data.relational.core.mapping.Column;

public record CommunityFollowKey(
    @Column("user_id") UUID userId,
    @Column("community_id") Long communityId
) {
    public static CommunityFollowKey create(UUID userId, long communityId){
        return new CommunityFollowKey(userId, communityId);
    }
}
