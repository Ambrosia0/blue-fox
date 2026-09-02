package com.ambrosia.comment_service.kafka.producer;

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
        var id = switch(commentEvent.getEventCase()){
            case CREATED -> commentEvent.getCreated().getId();
            case DELETED -> commentEvent.getDeleted().getId();
            default -> throw new RuntimeException("Unexpected event!");
        };
        kafkaTemplate.send(Topics.COMMENT_EVENT, Long.toString(id), commentEvent.toByteArray());
    }
}
