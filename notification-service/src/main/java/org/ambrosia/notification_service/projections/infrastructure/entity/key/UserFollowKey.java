package org.ambrosia.notification_service.projections.infrastructure.entity.key;

import java.util.UUID;

public record UserFollowKey(
    UUID userId,
    UUID followedUserId
) {
    public static UserFollowKey from(UUID userId, UUID followedUserId){
        return new UserFollowKey(userId, followedUserId);
    }
}
