package org.ambrosia.notification_service.projections.infrastructure.persistence;

import com.ambrosia.profile_service.kafka_events.UserEvent;
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface UserRepository {
    Uni<Void> process(List<UserEvent> toUpsert, List<UserEvent> toDelete);
}
