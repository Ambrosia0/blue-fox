package org.ambrosia.notification_service.kafka.event;

import com.google.protobuf.InvalidProtocolBufferException;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.ambrosia.notification_service.event.application.UserEventDispatcher;
import org.ambrosia.notification_service.kafka.consumer.NotificationConsumer;
import org.ambrosia.notification_service.kafka_events.Notification;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class NotificationEvent {
    private static Logger LOG = LoggerFactory.getLogger(NotificationConsumer.class);
    
    private final UserEventDispatcher userEventDispatcher;
    
    public NotificationEvent(UserEventDispatcher userEventDispatcher) {
        this.userEventDispatcher = userEventDispatcher;
    }
    
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    @Incoming("notification-event")
    public void consume(ConsumerRecords<String, byte[]> messages){
        for(ConsumerRecord<String, byte[]> message: messages){
            try {
                userEventDispatcher.sendMessage(Notification.parseFrom(message.value()));
            } catch (InvalidProtocolBufferException e){
                LOG.error("[Notification Event] Invalid message body!", e);
            }
        }
    }
}
