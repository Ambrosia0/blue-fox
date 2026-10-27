package org.ambrosia.notification_service.projections.infrastructure.persistence;

import com.ambrosia.community_service.kafka_events.CommunityBan;
import com.ambrosia.community_service.kafka_events.CommunityBanEvent;
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface CommunityBanRepository {
    Uni<Void> process(List<CommunityBanEvent> toInsert, List<CommunityBanEvent> toDelete);
}
