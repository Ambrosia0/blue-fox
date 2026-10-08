package com.ambrosia.community_service.infrastructure.kafka.outbox;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.ambrosia.community_service.kafka_events.CommunityEvent;
import com.ambrosia.library_core.dto.Topics;
import com.ambrosia.outbox.entity.KafkaOutbox;
import com.ambrosia.outbox.kafka.KafkaOutboxConverter;

/**
 * Converter for kafka outbox entity
 * @see KafkaOutbox
 */
@Component
public class KafkaOutboxConverterImpl implements KafkaOutboxConverter<CommunityEvent>{
    @Override
    public Class<CommunityEvent> getSourceType() {
        return CommunityEvent.class;
    }

    @Override
    public KafkaOutbox convert(CommunityEvent source) {
        Assert.notNull(source, "Object must be not null!");
        var casted = (CommunityEvent)source;
        var id = switch(casted.getEventCase()){
            case CREATE, DELETE, UPDATE -> casted.getId();
            default -> throw new RuntimeException("Unknown body!");
        };

        return KafkaOutbox.from(
            Long.toString(id),
            Topics.COMMUNITY,
            casted.toByteArray()
        );
    }
}


