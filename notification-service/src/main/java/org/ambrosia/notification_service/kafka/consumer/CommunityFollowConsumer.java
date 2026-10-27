package org.ambrosia.notification_service.kafka.consumer;

import com.ambrosia.community_service.kafka_events.CommunityFollowEvent;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.ambrosia.notification_service.projections.infrastructure.entity.key.CommunityFollowKey;
import org.ambrosia.notification_service.projections.infrastructure.persistence.impl.VertxCommunityFollowRepository;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.google.protobuf.InvalidProtocolBufferException;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ApplicationScoped 
public class CommunityFollowConsumer {
    private final Logger LOG = LoggerFactory.getLogger(CommunityFollowConsumer.class);

    @Inject
    VertxCommunityFollowRepository vertxCommunityFollowRepository;

    @Incoming("community-follow")
    public Uni<Void> consume(ConsumerRecords<String, byte[]> messages) {
        var parsed = StreamSupport.stream(messages.spliterator(), false)
            .map(t -> {
                try {
                    return CommunityFollowEvent.parseFrom(t.value());
                } catch (InvalidProtocolBufferException e) {
                    LOG.error("[Community Follow Batch] Invalid message body!", e);
                    throw new RuntimeException(e);
                }
            })
            .collect(Collectors.toMap(k ->
                        CommunityFollowKey.from(UUID.fromString(k.getRequestingUser()), k.getCommunityId()),
                    CommunityFollowEvent::getFollowed,
                    (oldVal,newVal) -> // isFollowed
                        newVal?
                            oldVal == false? // prev isFollowed true && new isFollowed false
                                null: // cancels element addition
                                newVal: // merges if create && create
                            oldVal == true? // prev isFollowed false (to delete) && new isFollowed true (to create)
                                null: // cancels element addition
                                newVal // merges if delete && delete
                )
            )
            .entrySet()
            .stream()
            .collect(Collectors.partitioningBy(
                Map.Entry::getValue,
                Collectors.mapping(Map.Entry::getKey, Collectors.toList())
            ));
        return vertxCommunityFollowRepository.process(
            parsed.get(true),
            parsed.get(false)
        );
    }
}
