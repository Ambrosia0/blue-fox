package com.ambrosia.content_service.kafka.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.ambrosia.comment_service.kafka_events.CommentEvent;

public class CommentEventSerde implements Serde<CommentEvent>{
    private final static Deserializer<CommentEvent> DESERIALIZER = (topic, data) -> {
        try {
            return CommentEvent.parseFrom(data);
        } catch (Exception e) {
            throw new RuntimeException("Invalid body!");
        }
    };
    private final static Serializer<CommentEvent> SERIALIZER = (topic, data) -> {
        return data.toByteArray();
    };

    @Override
    public Deserializer<CommentEvent> deserializer() {
        return DESERIALIZER;
    }

    @Override
    public Serializer<CommentEvent> serializer() {
        return SERIALIZER;
    }
}
