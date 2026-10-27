package org.ambrosia.notification_service.event.infrastructure.entity;

import java.util.UUID;

public record UserFollow(
    UUID followedUserId,
    Long communityId
) {}
