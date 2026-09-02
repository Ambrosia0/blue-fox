package com.ambrosia.outbox.kafka;

import com.ambrosia.outbox.entity.KafkaOutbox;

/**
 * Converter for outboxed kafka events
 * @param <T> type of source objects
 */
public interface KafkaOutboxConverter<T> {
    /**
     * Returns source type handled by this converter
     * <p> Preferable to obtaining type, as spring beans can be proxied </p>
     * @return source object type
     */
    Class<T> getSourceType();
    KafkaOutbox convert(Object source);
}
