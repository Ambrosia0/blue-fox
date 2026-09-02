package com.ambrosia.content_service.kafka.outbox;

import org.springframework.stereotype.Component;

import com.ambrosia.content_service.kafka_events.PostEvent;
import com.ambrosia.library_core.dto.Topics;
import com.ambrosia.outbox.entity.KafkaOutbox;
import com.ambrosia.outbox.kafka.KafkaOutboxConverter;

@Component
public class KafkaPostEventOutboxConverter implements KafkaOutboxConverter<PostEvent>{
    @Override
    public Class<PostEvent> getSourceType() {
        return PostEvent.class;
    }

    @Override
    public KafkaOutbox convert(Object source) {
        var event = (PostEvent) source;
        var id = switch(event.getEventCase()){
            case CREATED -> event.getCreated().getId();
            case DELETED -> event.getDeleted().getId();
            default -> throw new RuntimeException("Unknown body!");
        };
        return KafkaOutbox.from(
            Long.toString(id), 
            Topics.POST_EVENT,
            event.toByteArray()
        );
    }
}
