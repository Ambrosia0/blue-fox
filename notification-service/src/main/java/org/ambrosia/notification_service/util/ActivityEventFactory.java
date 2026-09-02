package org.ambrosia.notification_service.util;

import java.time.Instant;

import org.ambrosia.notification_service.kafka_events.ActivityEvent;
import org.ambrosia.notification_service.kafka_events.ActivityEventType;


public class ActivityEventFactory {
    public static ActivityEvent create(String userId, ActivityEventType type){
        return ActivityEvent.newBuilder()
            .setUserId(userId)
            .setType(type)
            .setTimestamp(Instant.now().getEpochSecond())
            .build();
    }
}
