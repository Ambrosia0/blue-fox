package org.ambrosia.notification_service.projections.infrastructure.persistence;

import com.ambrosia.community_service.kafka_events.CommunityEvent;
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface CommunityRepository {
    Uni<Void> process(List<CommunityEvent> toUpsert, List<CommunityEvent> toDelete);
}
