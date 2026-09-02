package com.ambrosia.content_service.kafka.consumer;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.content_service.kafka_events.AggregatedPreviewEvent;
import com.ambrosia.content_service.post.service.PostInternalService;
import com.ambrosia.library_core.dto.Topics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class AggregatedPreviewConsumer {
    private final PostInternalService postInternalService;

    @KafkaListener(
        topics = Topics.PREVIEW_AGGREGATION,
        batch = "true",
        containerFactory = "kafkaViewAggregationListenerContainerFactory"
    )
    public void on(List<byte[]> message) throws Exception{
        var toIncrement = message.stream()
            .map(t -> {
                try {
                    return AggregatedPreviewEvent.parseFrom(t);
                } catch (Exception e) {
                    log.error("Unexpected message body! {}", e);
                    throw new RuntimeException("Unexpected message body!", e);
                }
            })
            .toList();
        postInternalService.incrementPreviewCount(toIncrement);
    }
}
