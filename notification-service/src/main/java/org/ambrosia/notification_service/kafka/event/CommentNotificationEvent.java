package org.ambrosia.notification_service.kafka.event;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.ambrosia.comment_service.kafka_events.CommentEvent;
import com.google.protobuf.util.JsonFormat;

import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped 
public class CommentNotificationEvent {
    private static Logger LOG = LoggerFactory.getLogger(CommentNotificationEvent.class);
    
    private final EventBus eventBus;
    
    public CommentNotificationEvent(EventBus eventBus) {
        this.eventBus = eventBus;
    }
    
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    @Incoming("comment-channel")
    public void consume(ConsumerRecords<String, byte[]> messages) {
        for(ConsumerRecord<String, byte[]> message: messages){
            try {
                var parsed = CommentEvent.parseFrom(message.value());
                eventBus.publish(
                    "event.broadcast.post."+parsed.getPostId(),
                    JsonFormat.printer().print(parsed)
                );
            } catch (InvalidProtocolBufferException e){
                LOG.error("[Comment Event] Invalid message body!", e);
            }
        }
    }
}
