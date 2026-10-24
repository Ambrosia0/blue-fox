package com.ambrosia.comment_service.infrastructure.kafka.outbox;

import com.ambrosia.comment_service.kafka_events.CommentEvent;
import com.ambrosia.library_core.dto.Topics;
import com.ambrosia.outbox.entity.KafkaOutbox;
import com.ambrosia.outbox.kafka.KafkaOutboxConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Converter for comment outbox event
 * @see CommentEvent
 */
@Component
public class CommentKafkaOutboxConverter implements KafkaOutboxConverter<CommentEvent> {
    @Override
    public Class<CommentEvent> getSourceType() {
        return CommentEvent.class;
    }
    
    @Override
    public KafkaOutbox convert(CommentEvent source) {
        Assert.notNull(source, "Object must not be null!");
        return KafkaOutbox.from(
            Long.toString(source.getCommentId()),
            Topics.COMMENT_EVENT,
            ((CommentEvent) source).toByteArray()
        );
    }
}
