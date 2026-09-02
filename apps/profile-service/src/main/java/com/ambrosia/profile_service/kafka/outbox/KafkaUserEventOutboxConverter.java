package com.ambrosia.profile_service.kafka.outbox;

import org.springframework.stereotype.Component;

import com.ambrosia.library_core.dto.Topics;
import com.ambrosia.outbox.entity.KafkaOutbox;
import com.ambrosia.outbox.kafka.KafkaOutboxConverter;
import com.ambrosia.profile_service.kafka_events.UserEvent;

@Component
public class KafkaUserEventOutboxConverter implements KafkaOutboxConverter<UserEvent>{
    @Override
    public Class<UserEvent> getSourceType() {
        return UserEvent.class;
    }

    @Override
    public KafkaOutbox convert(Object source) {
        var casted = (UserEvent) source;
        var key = switch(casted.getPayloadCase()){
            case CREATED -> casted.getCreated().getId();
            default -> throw new RuntimeException("Unknown body payload");
        };
        return KafkaOutbox.from(
            key,
            Topics.USER_EVENT,
            casted.toByteArray()
        );
    }
}
