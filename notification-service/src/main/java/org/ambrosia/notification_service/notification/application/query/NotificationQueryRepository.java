package org.ambrosia.notification_service.notification.application.query;

import io.smallrye.mutiny.Uni;
import org.ambrosia.notification_service.notification.api.dto.NotificationFilter;
import org.ambrosia.notification_service.notification.api.dto.NotificationResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationQueryRepository {
    Uni<List<NotificationResponse>> getNotifications(NotificationFilter filter, UUID userId);
    Uni<Long> countNew(UUID userId);
}
