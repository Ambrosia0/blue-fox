package com.ambrosia.content_service.kafka.producer;

import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.ambrosia.content_service.kafka_events.PostLikeNotification;
import com.ambrosia.library_core.dto.Topics;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PostLikeEventProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @EventListener
    public void on(PostLikeNotification postLikeNotification){
        kafkaTemplate.send(
            Topics.POST_LIKE_UPDATE, postLikeNotification.toByteArray());
    }
}
