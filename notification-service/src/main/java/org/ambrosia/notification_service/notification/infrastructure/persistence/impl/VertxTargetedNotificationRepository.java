package org.ambrosia.notification_service.notification.infrastructure.persistence.impl;

import com.github.f4b6a3.uuid.UuidCreator;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import org.ambrosia.notification_service.kafka_events.Notification;
import org.ambrosia.notification_service.notification.infrastructure.persistence.TargetedNotificationRepository;

import java.util.List;

@ApplicationScoped
public class VertxTargetedNotificationRepository implements TargetedNotificationRepository {
    private final Pool pool;
    
    public VertxTargetedNotificationRepository(Pool pool) {
        this.pool = pool;
    }
    
    public Uni<Void> process(List<Notification> notifications) {
        return pool.withTransaction(conn -> conn
            .preparedQuery("INSERT INTO targeted_notification(id, user_id, content) VALUES ($1, $2, $3)")
            .executeBatch(notifications.stream()
                .map(t -> Tuple.of(
                        UuidCreator.getTimeOrderedEpoch(),
                        t.getTargetedNotification().getUserId(),
                        t.getTargetedNotification().getContent()
                    )
                )
                .toList()
            )
            .replaceWithVoid()
        );
    }
}
