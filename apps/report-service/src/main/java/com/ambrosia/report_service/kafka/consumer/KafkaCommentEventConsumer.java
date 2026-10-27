package com.ambrosia.report_service.kafka.consumer;

import com.ambrosia.report_service.comment.repository.CommentProjectionRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.comment_service.kafka_events.CommentEvent;
import com.ambrosia.library_core.dto.Topics;
import com.ambrosia.report_service.comment.service.CommentProjectionService;
import com.google.protobuf.InvalidProtocolBufferException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaCommentEventConsumer {
    private final CommentProjectionRepository commentProjectionRepository;

    @KafkaListener(
        topics = Topics.COMMENT_EVENT,
        errorHandler = "serializationErrorHandler"
    )
    public void on(List<byte[]> message){
        var parsed = message.stream()
            .map(m -> {
                try {
                    return CommentEvent.parseFrom(m);
                } catch (InvalidProtocolBufferException ex){
                    log.error("Invalid message body in comment event!", ex);
                    throw new RuntimeException(ex);
                }
            })
            .collect(Collectors.partitioningBy(e -> e.hasCreated() || e.hasUpdate()));
        commentProjectionRepository.process(parsed.get(true), parsed.get(false));
    }
}
