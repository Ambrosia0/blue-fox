package com.ambrosia.report_service.kafka.consumer;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.comment_service.kafka_events.CommentEvent;
import com.ambrosia.library_core.dto.Topics;
import com.ambrosia.report_service.comment.service.CommentProjectionService;
import com.google.protobuf.InvalidProtocolBufferException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaCommentEventConsumer {
    private final CommentProjectionService commentProjectionService;

    @KafkaListener(
        topics = Topics.COMMENT_EVENT,
        errorHandler = "serializationErrorHandler"
    )
    public void on(byte[] message){
        try {
            var parsedMessage = CommentEvent.parseFrom(message);
            if(parsedMessage.hasCreated())
                commentProjectionService.create(
                    parsedMessage.getCreated(),
                    UUID.fromString(parsedMessage.getEventId())
                );
            else if(parsedMessage.hasDeleted())
                commentProjectionService.delete(
                    parsedMessage.getDeleted(),
                    UUID.fromString(parsedMessage.getEventId())
                );
        } catch (InvalidProtocolBufferException e) {
            log.error("Invalid message format!", e);
            throw new RuntimeException(e);
        }
    }
}
