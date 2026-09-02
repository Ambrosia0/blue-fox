package com.ambrosia.comment_service.kafka.consumer;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.comment_service.post.service.PostProjectionCreator;
import com.ambrosia.content_service.kafka_events.PostEvent;
import com.ambrosia.library_core.dto.Topics;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KafkaPostProjectionConsumer {
    private final PostProjectionCreator postProjectionCreator;

    @KafkaListener(
        topics = Topics.POST_EVENT,
        groupId = "comment-service",
        errorHandler = "serializationErrorHandler",
        batch = "true"
    )
    public void consume(List<ConsumerRecord<String, byte[]>> messages) throws Exception{
        var parsedMessage = messages.stream()
            .collect(Collectors.toMap(
                ConsumerRecord::key,
                record -> {
                    try {
                        return PostEvent.parseFrom(record.value());
                    } catch (Exception e) {
                        throw new RuntimeException("Invalid body format!", e);
                    }
                },
                (oldVal, newVal) -> newVal
            ));
        var batches = parsedMessage.values()
            .stream()
            .collect(Collectors.partitioningBy(PostEvent::hasCreated));
        postProjectionCreator.process(batches.get(true), batches.get(false));
    }
}
