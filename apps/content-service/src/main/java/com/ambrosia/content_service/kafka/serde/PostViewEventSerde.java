package com.ambrosia.content_service.kafka.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.ambrosia.content_service.kafka_events.PostViewEvent;

import lombok.SneakyThrows;

public class PostViewEventSerde implements Serde<PostViewEvent>{
    private final PostViewEventDeserializer deserializer = new PostViewEventDeserializer();
    private final PostViewEventSerializer serializer = new PostViewEventSerializer();

    @Override
    public Deserializer<PostViewEvent> deserializer() {
        return deserializer;
    }

    @Override
    public Serializer<PostViewEvent> serializer() {
        return serializer;
    }

    public class PostViewEventDeserializer implements Deserializer<PostViewEvent> {
        @SneakyThrows
        @Override
        public PostViewEvent deserialize(String topic, byte[] data) {
            return PostViewEvent.parseFrom(data);
        }
    }

    public class PostViewEventSerializer implements Serializer<PostViewEvent> {
        @Override
        public byte[] serialize(String topic, PostViewEvent data) {
            return data.toByteArray();
        }
    }
}
