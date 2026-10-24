package org.ambrosia.notification_service.event.domain;

import io.smallrye.mutiny.Uni;
import org.ambrosia.notification_service.event.infrastructure.entity.UserFollow;

import java.util.List;
import java.util.UUID;

public interface UserFollowRepository {
    Uni<List<UserFollow>> getFollows(UUID userId);
}
