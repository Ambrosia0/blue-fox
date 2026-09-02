package com.ambrosia.community_service.kafka.outbox;

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
    public KafkaOutbox convert(Object source) {
        Assert.notNull(source, "Object must be not null!");
        var casted = (CommunityEvent)source;
        var id = switch(casted.getEventCase()){
            case CREATE -> casted.getCreate().getId();
            case DELETE -> casted.getDelete().getId();
            case UPDATE -> casted.getUpdate().getId();
            default -> throw new RuntimeException("Unknown body!");
        };

        return KafkaOutbox.from(
            Long.toString(id),
            Topics.COMMUNITY,
            casted.toByteArray()
        );
    }
}


