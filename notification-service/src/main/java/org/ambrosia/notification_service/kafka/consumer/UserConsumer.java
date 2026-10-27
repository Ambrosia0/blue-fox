package org.ambrosia.notification_service.kafka.consumer;

import com.ambrosia.profile_service.kafka_events.UserEvent;
import com.google.protobuf.InvalidProtocolBufferException;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.ambrosia.notification_service.projections.infrastructure.persistence.UserRepository;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ApplicationScoped
public class UserConsumer {
    private final static Logger LOG = LoggerFactory.getLogger(UserConsumer.class);
    
    private final UserRepository userRepository;
    
    public UserConsumer(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    
    @Incoming("user")
    public Uni<Void> on(ConsumerRecords<String, byte[]> messages){
        var parsed = StreamSupport.stream(messages.spliterator(), false)
            .map(message -> {
                try {
                    return UserEvent.parseFrom(message.value());
                } catch (InvalidProtocolBufferException e) {
                    LOG.error("[User Batch] Invalid message body!", e);
                    throw new RuntimeException(e);
                }
            })
            .collect(Collectors.partitioningBy(event -> event.hasCreated() || event.hasUpdated()));
        return userRepository.process(
            parsed.get(true),
            parsed.get(false)
        );
    }
}
