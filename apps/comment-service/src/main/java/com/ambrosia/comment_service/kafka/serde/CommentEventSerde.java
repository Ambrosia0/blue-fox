package com.ambrosia.comment_service.kafka.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.ambrosia.comment_service.kafka_events.CommentEvent;

import lombok.SneakyThrows;

public class CommentEventSerde implements Serde<CommentEvent>{
    private final CommentEventDeserializer deserializer = new CommentEventDeserializer();
    private final CommentEventSerializer serializer = new CommentEventSerializer();

    @Override
    public Deserializer<CommentEvent> deserializer() {
        return deserializer;
    }

    @Override
    public Serializer<CommentEvent> serializer() {
        return serializer;
    }

    public class CommentEventDeserializer implements Deserializer<CommentEvent>{
        @SneakyThrows
        @Override
        public CommentEvent deserialize(String topic, byte[] data) {
            return CommentEvent.parseFrom(data);
        }
    }

    public class CommentEventSerializer implements Serializer<CommentEvent>{
        @Override
        public byte[] serialize(String topic, CommentEvent data) {
            return data.toByteArray();
        }
    }
}
