package org.ambrosia.notification_service.kafka;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.ambrosia.profile_service.kafka_events.UserAggregation;
import com.google.protobuf.util.JsonFormat;

import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ContentNotificationEvent {
    @Inject
    private EventBus eventBus;

    @Incoming("content-channel")
    public void postEvent(byte[] event) throws Exception {
        var body = UserAggregation.parseFrom(event);
        switch (body.getPayloadCase()) {
            case COMMENT ->{
                var comment = body.getComment();
                eventBus.publish("event.broadcast.post."+comment.getPostId(), JsonFormat.printer().print(body));
            }
            case POST -> {
                eventBus.publish("event.broadcast", JsonFormat.printer().print(body));
            }
            default -> {}
        }
    }
}
