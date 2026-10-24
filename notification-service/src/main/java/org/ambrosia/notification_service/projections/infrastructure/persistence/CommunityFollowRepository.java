package org.ambrosia.notification_service.projections.infrastructure.persistence;

import io.smallrye.mutiny.Uni;
import org.ambrosia.notification_service.projections.infrastructure.entity.key.CommunityFollowKey;

import java.util.List;

public interface CommunityFollowRepository {
    Uni<Void> process(List<CommunityFollowKey> toSave, List<CommunityFollowKey> toDelete);
}
