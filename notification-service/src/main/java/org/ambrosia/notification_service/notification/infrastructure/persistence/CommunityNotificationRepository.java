package org.ambrosia.notification_service.notification.infrastructure.persistence;

import io.smallrye.mutiny.Uni;
import org.ambrosia.notification_service.kafka_events.Notification;

import java.util.List;

public interface CommunityNotificationRepository {
    Uni<Void> process(List<Notification> notifications);
}
