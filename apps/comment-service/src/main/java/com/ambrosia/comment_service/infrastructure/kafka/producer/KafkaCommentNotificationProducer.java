package com.ambrosia.comment_service.infrastructure.kafka.producer;

import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.ambrosia.comment_service.kafka_events.CommentEvent;
import com.ambrosia.library_core.dto.Topics;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KafkaCommentNotificationProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @EventListener
    public void on(CommentEvent commentEvent){
        kafkaTemplate.send(
            Topics.COMMENT_EVENT, 
            Long.toString(commentEvent.getCommentId()), 
            commentEvent.toByteArray()
        );
    }
}
