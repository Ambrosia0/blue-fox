package com.ambrosia.report_service.kafka.consumer;

import com.ambrosia.report_service.post.repository.PostProjectionRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.library_core.dto.Topics;
import com.ambrosia.content_service.kafka_events.PostEvent;
import com.ambrosia.report_service.post.service.PostProjectionService;
import com.google.protobuf.InvalidProtocolBufferException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaPostEventConsumer {
    private final PostProjectionRepository postProjectionRepository;

    @KafkaListener(
        topics = Topics.POST_EVENT,
        errorHandler = "serializationErrorHandler"
    )
    public void on(List<byte[]> messages){
        var parsed = messages.stream()
            .map(m -> {
                try {
                    return PostEvent.parseFrom(m);
                } catch (InvalidProtocolBufferException ex){
                    log.error("Invalid message body in post event!",ex);
                    throw new RuntimeException(ex);
                }
            })
            .collect(Collectors.partitioningBy(e -> e.hasUpdated() || e.hasCreated()));
        postProjectionRepository.process(parsed.get(true), parsed.get(false));
    }
}
