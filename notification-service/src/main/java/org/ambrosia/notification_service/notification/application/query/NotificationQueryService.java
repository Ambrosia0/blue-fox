package org.ambrosia.notification_service.notification.application.query;

import io.smallrye.mutiny.Uni;
import org.ambrosia.notification_service.core.policy.Actor;
import org.ambrosia.notification_service.notification.api.dto.NotificationFilter;
import org.ambrosia.notification_service.notification.api.dto.NotificationResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationQueryService {
    Uni<List<NotificationResponse>> getNotifications(NotificationFilter filter, Actor actor);
    Uni<Long> getNewNotificationsCount(Actor actor);
}
