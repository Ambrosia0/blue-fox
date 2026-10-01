package com.ambrosia.collaboration_service.kafka;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.content_service.kafka_events.collaboration.CollaborationEvent;
import com.google.protobuf.InvalidProtocolBufferException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@RequiredArgsConstructor 
@Component 
public class KafkaCollaborationEventConsumer {
    private final ApplicationEventPublisher applicationEventPublisher;

    @KafkaListener(
        topics = "blog.post.collaboration"
    )
    public void consume(byte[] message){
        try {
            var parsed = CollaborationEvent.parseFrom(message);
            applicationEventPublisher.publishEvent(parsed);
        } catch (InvalidProtocolBufferException e) {
            log.error("Unknown collaboration event body!", e);
        }
    }
}
