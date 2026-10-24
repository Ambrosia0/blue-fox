package org.ambrosia.notification_service.utils;

import org.ambrosia.notification_service.notification.api.dto.NotificationFilter;

import java.time.Instant;
import java.util.UUID;

public class NotificationFilterBuilder {
    private Instant lastSeenInstant;
    private UUID lastSeenId;
    
    public NotificationFilterBuilder lastSeenInstant(Instant instant){
        this.lastSeenInstant = instant;
        return this;
    }
    
    public NotificationFilterBuilder lastSeenId(UUID id){
        this.lastSeenId = id;
        return this;
    }
    
    public NotificationFilter build(){
        return new NotificationFilter(
            lastSeenInstant,
            lastSeenId
        );
    }
    
    public static NotificationFilterBuilder builder(){
        return new NotificationFilterBuilder();
    }
}
