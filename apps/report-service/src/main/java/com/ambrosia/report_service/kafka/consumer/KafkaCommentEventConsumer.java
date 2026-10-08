package com.ambrosia.report_service.kafka.consumer;

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
            commentProjectionService.process(parsedMessage);
        } catch (InvalidProtocolBufferException e) {
            log.error("Invalid message format!", e);
            throw new RuntimeException(e);
        }
    }
}
