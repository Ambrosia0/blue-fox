package com.ambrosia.content_service.kafka.consumer;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.content_service.kafka_events.PostDelta;
import com.ambrosia.content_service.post.service.PostInternalService;
import com.ambrosia.library_core.dto.Topics;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AggregateCommentCountConsumer {
    private final PostInternalService postInternalService;

    @KafkaListener(
        topics = Topics.POST_COMMENT_COUNT_AGGREGATE,
        batch = "true",
        groupId = "content-service",
        errorHandler = "serializationErrorHandler",
        containerFactory = "kafkaCommentCountAggregationListenerContainerFactory"
    )
    public void consume(List<byte[]> message){
        var batch = message.stream()
            .map(t -> {
                try {
                    return PostDelta.parseFrom(t);
                } catch (Exception e) {
                    throw new RuntimeException("Unexpected body!");
                }
            })
            .toList();
        postInternalService.incrementCommentCount(batch);
    }
}
