package org.ambrosia.notification_service.event.application;

import io.smallrye.mutiny.Multi;
import jakarta.ws.rs.sse.OutboundSseEvent;

import java.util.UUID;

public interface UserActivityRegistry {
    Multi<OutboundSseEvent> register(UUID userId);
}
