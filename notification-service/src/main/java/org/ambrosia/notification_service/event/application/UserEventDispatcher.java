package org.ambrosia.notification_service.event.application;

import org.ambrosia.notification_service.kafka_events.Notification;

public interface UserEventDispatcher {
    void sendMessage(Notification notification);
}
