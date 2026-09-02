package com.ambrosia.content_service.kafka.producer;

import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.ambrosia.content_service.kafka_events.PostPreviewEvent;
import com.ambrosia.library_core.dto.Topics;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PostPreviewEventProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @EventListener
    void on(PostPreviewEvent previewEvent){
        kafkaTemplate.send(
            Topics.PREVIEW_EVENT, 
            previewEvent.toByteArray());
    }
}
