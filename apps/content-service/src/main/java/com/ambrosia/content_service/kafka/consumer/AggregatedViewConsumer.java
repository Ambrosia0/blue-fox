package com.ambrosia.content_service.kafka.consumer;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.content_service.kafka_events.AggregatedViewEvent;
import com.ambrosia.content_service.post.service.PostInternalService;
import com.ambrosia.library_core.dto.Topics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class AggregatedViewConsumer {
    private final PostInternalService postInternalService;

    @KafkaListener(
        topics = Topics.VIEW_AGGREGATION,
        batch = "true",
        containerFactory = "kafkaViewAggregationListenerContainerFactory",
        errorHandler = "serializationErrorHandler"
    )
    void on(List<byte[]> message) throws Exception{
        var toIncrement = message.stream()
            .map(body -> {
                try {
                    return AggregatedViewEvent.parseFrom(body);   
                } catch (Exception e) {
                    log.error("Unexpected body! {}", e);
                    throw new RuntimeException("Unexpected body!");
                }
            })
            .toList();
        postInternalService.incrementViewCount(toIncrement);

    }
}
