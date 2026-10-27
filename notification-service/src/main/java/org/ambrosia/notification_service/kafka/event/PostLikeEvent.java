package org.ambrosia.notification_service.kafka.event;

import com.ambrosia.content_service.kafka_events.PostLikeNotification;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.ambrosia.comment_service.kafka_events.CommentLikeNotification;
import com.google.protobuf.util.JsonFormat;

import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PostLikeEvent {
    @Inject
    EventBus eventBus;
    
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    @Incoming("post-like-channel")
    public void likeEvent(byte[] event) throws Exception {
        var body = PostLikeNotification.parseFrom(event);
        body.getChangesMap()
            .forEach((key, value) -> {
                try {
                    eventBus.publish("event.broadcast.post." + key, value);
                } catch (Exception e) {
                    throw new RuntimeException("Error while serializing notification!" + body.getClass().getName(), e);
                }
            });
    }
}
