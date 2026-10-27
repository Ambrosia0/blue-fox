package org.ambrosia.notification_service.projections.infrastructure.persistence;

import com.ambrosia.content_service.kafka_events.PostEvent;
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface PostRepository {
    Uni<Void> process(List<PostEvent> toInsert, List<PostEvent> toDelete);
}
