package com.ambrosia.comment_service.kafka.producer;

import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.ambrosia.comment_service.kafka_events.CommentLikeNotification;
import com.ambrosia.library_core.dto.Topics;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KafkaCommentLikeUpdateProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @EventListener
    void on(CommentLikeNotification commentLikeNotification){
        kafkaTemplate.send(Topics.COMMENT_LIKE_UPDATE, commentLikeNotification.toByteArray());
    }
}
