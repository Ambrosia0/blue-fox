package org.ambrosia.notification_service.kafka;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.ambrosia.comment_service.kafka_events.CommentLikeNotification;
import com.google.protobuf.util.JsonFormat;

import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PostLikeEvent {
    @Inject
    private EventBus eventBus;

    @Incoming("post-like-channel")
    public void likeEvent(byte[] event) throws Exception {
        var body = CommentLikeNotification.parseFrom(event);
        body.getChangesMap().entrySet()
            .forEach(entry -> {
                try {
                    eventBus.publish("event.broadcast.post."+entry.getKey(), JsonFormat.printer().print(entry.getValue()));
                } catch (Exception e) {
                    throw new RuntimeException("Error while serializing notification!" + body.getClass().getName(), e);
                }
            });
    }
}
