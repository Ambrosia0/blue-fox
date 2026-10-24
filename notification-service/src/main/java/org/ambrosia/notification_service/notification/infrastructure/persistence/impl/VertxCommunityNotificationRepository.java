package org.ambrosia.notification_service.notification.infrastructure.persistence.impl;

import com.github.f4b6a3.uuid.UuidCreator;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.ambrosia.notification_service.kafka_events.Notification;
import org.ambrosia.notification_service.notification.infrastructure.persistence.CommunityNotificationRepository;

import java.util.List;

@ApplicationScoped
public class VertxCommunityNotificationRepository implements CommunityNotificationRepository {
    private final Pool pool;
    
    public VertxCommunityNotificationRepository(Pool pool) {
        this.pool = pool;
    }
    
    public Uni<Void> process(List<Notification> notifications){
        if(notifications == null || notifications.isEmpty())
            return Uni.createFrom().voidItem();
        return pool.preparedQuery("""
                INSERT INTO community_notification(id, community_id, content) VALUES ($1, $2, $3)
                """)
            .executeBatch(notifications.stream()
                .map(t -> Tuple.of(
                        UuidCreator.getTimeOrderedEpoch(),
                        t.getCommunityNotification().getCommunityId(),
                        t.getCommunityNotification().getContent()
                    )
                )
                .toList()
            )
            .replaceWithVoid();
    }
}
