package org.ambrosia.notification_service.event.application;

import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.core.eventbus.Message;
import org.ambrosia.notification_service.core.policy.Actor;

public interface PostEventRegistry {
    Multi<Message<String>> register(Long postId, Actor actor);
}
