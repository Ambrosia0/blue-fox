package org.ambrosia.notification_service.event.application;

import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.core.eventbus.Message;

import java.util.UUID;

public interface UserEventRegistry {
    Multi<Message<String>> register(UUID userId);
}
