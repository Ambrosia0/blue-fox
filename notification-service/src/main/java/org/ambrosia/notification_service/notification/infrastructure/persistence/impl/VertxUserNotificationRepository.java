package org.ambrosia.notification_service.notification.infrastructure.persistence.impl;

import com.github.f4b6a3.uuid.UuidCreator;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.ambrosia.notification_service.kafka_events.Notification;
import org.ambrosia.notification_service.notification.infrastructure.persistence.UserNotificationRepository;

import java.util.List;

@ApplicationScoped
public class VertxUserNotificationRepository implements UserNotificationRepository {
    private final Pool pool;
    
    public VertxUserNotificationRepository(Pool pool) {
        this.pool = pool;
    }
    
    public Uni<Void> process(List<Notification> messages){
        if(messages == null || messages.isEmpty())
            return Uni.createFrom().voidItem();
        return pool.withConnection(conn ->
            conn.preparedQuery("INSERT INTO user_notification(id, user_id, content) VALUES ($1, $2, $3)")
                .executeBatch(messages
                    .stream()
                    .map(t -> Tuple.of(
                            UuidCreator.getTimeOrderedEpoch(),
                            t.getUserNotification().getUserId(),
                            t.getUserNotification().getContent()
                        )
                    )
                    .toList()
                )
                .replaceWithVoid()
        );
    }
}
