package org.ambrosia.notification_service.notification.application.query.impl;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.ambrosia.notification_service.core.policy.Actor;
import org.ambrosia.notification_service.notification.api.dto.NotificationFilter;
import org.ambrosia.notification_service.notification.api.dto.NotificationResponse;
import org.ambrosia.notification_service.notification.application.query.NotificationQueryRepository;
import org.ambrosia.notification_service.notification.application.query.NotificationQueryService;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class NotificationQueryServiceImpl implements NotificationQueryService {
    private final NotificationQueryRepository notificationQueryRepository;
    
    public NotificationQueryServiceImpl(NotificationQueryRepository notificationQueryRepository) {
        this.notificationQueryRepository = notificationQueryRepository;
    }
    
    @Override
    public Uni<List<NotificationResponse>> getNotifications(NotificationFilter filter, Actor actor) {
        return notificationQueryRepository.getNotifications(filter, actor.id());
    }

    @Override
    public Uni<Long> getNewNotificationsCount(Actor actor) {
        return notificationQueryRepository.countNew(actor.id());
    }
}
