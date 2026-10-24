package org.ambrosia.notification_service.projections.infrastructure.persistence;

import io.smallrye.mutiny.Uni;
import org.ambrosia.notification_service.projections.infrastructure.entity.key.UserFollowKey;

import java.util.List;

public interface UserFollowRepository {
    Uni<Void> applyChanges(List<UserFollowKey> toSave, List<UserFollowKey> toDelete);
}
