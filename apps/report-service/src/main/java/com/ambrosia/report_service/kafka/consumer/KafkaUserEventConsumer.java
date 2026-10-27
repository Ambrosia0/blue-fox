package com.ambrosia.report_service.kafka.consumer;

import com.ambrosia.report_service.user.repository.UserProjectionRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.library_core.dto.Topics;
import com.ambrosia.profile_service.kafka_events.UserEvent;
import com.ambrosia.report_service.user.service.UserProjectionService;
import com.google.protobuf.InvalidProtocolBufferException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaUserEventConsumer {
    private final UserProjectionRepository userProjectionRepository;

    @KafkaListener(
        topics = Topics.USER_EVENT,
        errorHandler = "serializationErrorHandler"
    )
    public void on(List<byte[]> messages){
        var parsed = messages.stream()
            .map(m -> {
                try {
                    return UserEvent.parseFrom(m);
                }catch (InvalidProtocolBufferException ex){
                    log.error("Invalid message body in user event!", ex);
                    throw new RuntimeException(ex);
                }
            })
            .collect(Collectors.partitioningBy(e -> e.hasCreated() || e.hasUpdated()));
        userProjectionRepository.process(parsed.get(true), parsed.get(false));
    }
}
