package org.ambrosia.notification_service.notification.util;

import java.time.Instant;
import java.util.UUID;

import org.ambrosia.notification_service.kafka_events.ActivityEvent;
import org.ambrosia.notification_service.kafka_events.ActivityEventType;


public class ActivityEventFactory {
    public static ActivityEvent create(UUID userId, ActivityEventType type){
        return ActivityEvent.newBuilder()
            .setUserId(userId.toString())
            .setType(type)
            .setTimestamp(Instant.now().getEpochSecond())
            .build();
    }
}
