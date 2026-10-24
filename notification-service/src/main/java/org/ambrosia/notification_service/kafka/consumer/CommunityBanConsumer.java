package org.ambrosia.notification_service.kafka.consumer;

import com.ambrosia.community_service.kafka_events.CommunityBanEvent;
import com.google.protobuf.InvalidProtocolBufferException;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.ambrosia.notification_service.projections.infrastructure.persistence.CommunityBanRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@ApplicationScoped
public class CommunityBanConsumer {
    private final CommunityBanRepository communityBanRepository;
    
    private final Logger LOG = LoggerFactory.getLogger(CommunityBanConsumer.class);
    
    public CommunityBanConsumer(CommunityBanRepository communityBanRepository) {
        this.communityBanRepository = communityBanRepository;
    }
    
    @Incoming("community-ban")
    public Uni<Void> consume(ConsumerRecords<String, byte[]> message){
        var parsed = StreamSupport.stream(message.spliterator(), false)
            .collect(Collectors.toMap(
                    ConsumerRecord::key,
                    record -> {
                        try {
                            return CommunityBanEvent.parseFrom(record.value());
                        } catch (InvalidProtocolBufferException e) {
                            LOG.error("[Community Ban Batch] Invalid message body!", e);
                            throw new RuntimeException(e);
                        }
                    },
                    (oldVal, newVal) -> newVal
            ));
        var batches = parsed.values().stream()
            .collect(Collectors.partitioningBy(CommunityBanEvent::hasBan));
        return communityBanRepository.process(batches.get(true), batches.get(false));
    }
}
