package com.ambrosia.report_service.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.library_core.dto.Topics;
import com.ambrosia.content_service.kafka_events.PostEvent;
import com.ambrosia.report_service.post.service.PostProjectionService;
import com.google.protobuf.InvalidProtocolBufferException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaPostEventConsumer {
    private final PostProjectionService postProjectionService;

    @KafkaListener(
        topics = Topics.POST_EVENT,
        errorHandler = "serializationErrorHandler"
    )
    public void on(byte[] message){
        try {
            var parsedMessage = PostEvent.parseFrom(message);
            postProjectionService.process(parsedMessage);
        } catch (InvalidProtocolBufferException e) {
            log.error("Invalid message format!", e);
            throw new RuntimeException(e);
        }
    }
}
