package com.ambrosia.content_service.kafka.producer;

import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.ambrosia.content_service.kafka_events.PostViewEvent;
import com.ambrosia.library_core.dto.Topics;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PostViewEventProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @EventListener
    public void on(PostViewEvent viewEvent){
        kafkaTemplate.send(
            Topics.VIEW_EVENT,
            Long.toString(viewEvent.getPostId()),
            viewEvent.toByteArray());
    }
}
