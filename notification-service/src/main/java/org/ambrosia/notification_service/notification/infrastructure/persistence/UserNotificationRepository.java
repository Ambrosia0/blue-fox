package org.ambrosia.notification_service.notification.infrastructure.persistence;

import io.smallrye.mutiny.Uni;
import org.ambrosia.notification_service.kafka_events.Notification;

import java.util.List;

public interface UserNotificationRepository {
    Uni<Void> process(List<Notification> messages);
}
