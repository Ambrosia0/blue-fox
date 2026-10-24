package org.ambrosia.notification_service.projections.infrastructure.entity.key;

import java.util.UUID;

public record CommunityFollowKey(
    UUID userId,
    Long communityId
) {
    public static CommunityFollowKey from(UUID userId, Long communityId){
        return new CommunityFollowKey(userId, communityId);
    }
}
