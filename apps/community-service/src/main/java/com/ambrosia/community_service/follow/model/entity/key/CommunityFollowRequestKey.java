package com.ambrosia.community_service.follow.model.entity.key;

import java.util.UUID;

import org.springframework.data.relational.core.mapping.Column;

public record CommunityFollowRequestKey(
    @Column("user_id") UUID userId,
    @Column("community_id") Long communityId
) {
    public static CommunityFollowRequestKey create(UUID userId, Long communityId){
        return new CommunityFollowRequestKey(userId, communityId);
    }
}
