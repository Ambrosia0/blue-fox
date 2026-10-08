package com.ambrosia.content_service.infrastructure.kafka.outbox;

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
    public KafkaOutbox convert(PostEvent source) {
        var event = (PostEvent) source;
        return KafkaOutbox.from(
            Long.toString(event.getPostId()), 
            Topics.POST_EVENT,
            event.toByteArray()
        );
    }
}
