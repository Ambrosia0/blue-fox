package org.ambrosia.notification_service.kafka.consumer;

import com.ambrosia.content_service.kafka_events.PostEvent;
import com.google.protobuf.InvalidProtocolBufferException;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.ambrosia.notification_service.projections.infrastructure.persistence.PostRepository;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ApplicationScoped
public class PostConsumer {
    private final Logger LOG = LoggerFactory.getLogger(PostConsumer.class);
    
    private final PostRepository postRepository;
    
    public PostConsumer(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    
    @Incoming("post")
    public Uni<Void> on(ConsumerRecords<String, byte[]> messages){
        var parsed = StreamSupport.stream(messages.spliterator(), false)
            .map(message -> {
                try {
                    return PostEvent.parseFrom(message.value());
                } catch (InvalidProtocolBufferException ex){
                    LOG.error("[Post Batch] Invalid message body!", ex);
                    throw new RuntimeException(ex);
                }
            })
            .collect(Collectors.partitioningBy(event -> event.hasCreated() || event.hasUpdated()));
        return postRepository.process(parsed.get(true), parsed.get(false));
    }
}
