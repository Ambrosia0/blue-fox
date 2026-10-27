package org.ambrosia.notification_service.kafka.event;

import com.google.protobuf.InvalidProtocolBufferException;
import org.ambrosia.notification_service.kafka.consumer.NotificationConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.ambrosia.comment_service.kafka_events.CommentLikeNotification;
import com.google.protobuf.util.JsonFormat;

import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class CommentLikeEvent {
    private static Logger LOG = LoggerFactory.getLogger(CommentLikeEvent.class);
    
    @Inject
    EventBus eventBus;

    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    @Incoming("comment-like-channel")
    public void likeEvent(ConsumerRecords<String, byte[]> messages) throws Exception {
        for(ConsumerRecord<String, byte[]> message: messages){
            try {
                var parsed = CommentLikeNotification.parseFrom(message.value());
                parsed.getChangesMap()
                    .forEach((key, value) -> {
                        try {
                            eventBus.publish("event.broadcast.post."+key, JsonFormat.printer().print(value));
                        } catch (Exception e) {
                            throw new RuntimeException("Error while serializing notification!" + parsed.getClass().getName(), e);
                        }
                    });
            } catch (InvalidProtocolBufferException e){
                LOG.error("[Comment Like Event] Invalid message body!", e);
            }
        }
    }
}
