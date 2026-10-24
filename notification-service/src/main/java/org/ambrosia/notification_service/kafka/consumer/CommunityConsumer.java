package org.ambrosia.notification_service.kafka.consumer;

import com.ambrosia.community_service.kafka_events.CommunityEvent;
import com.google.protobuf.InvalidProtocolBufferException;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.ambrosia.notification_service.projections.infrastructure.persistence.CommunityRepository;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ApplicationScoped
public class CommunityConsumer {
    private final static Logger LOG = LoggerFactory.getLogger(CommunityConsumer.class);
    
    private final CommunityRepository communityRepository;
    
    public CommunityConsumer(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }
    
    @Incoming("community")
    public Uni<Void> on(ConsumerRecords<String, byte[]> messages){
        var parsed = StreamSupport.stream(messages.spliterator(), false)
            .map(message -> {
                try {
                    return CommunityEvent.parseFrom(message.value());
                } catch (InvalidProtocolBufferException e) {
                    LOG.error("[Community Batch] Invalid message body!", e);
                    throw new RuntimeException(e);
                }
            })
            .collect(Collectors.partitioningBy(event -> event.hasCreate() || event.hasUpdate()));
        return communityRepository.process(
            parsed.get(true),
            parsed.get(false)
        );
    }
}
