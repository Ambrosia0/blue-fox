package com.ambrosia.content_service.kafka.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.ambrosia.content_service.kafka_events.PostPreviewEvent;

import lombok.SneakyThrows;

public class PostPreviewEventSerde implements Serde<PostPreviewEvent>{
    private final PreviewEventDeserializer deserializer = new PreviewEventDeserializer();
    private final PreviewEventSerializer serializer = new PreviewEventSerializer();

    @Override
    public Deserializer<PostPreviewEvent> deserializer() {
        return deserializer;
    }

    @Override
    public Serializer<PostPreviewEvent> serializer() {
        return serializer;
    }

    public class PreviewEventDeserializer implements Deserializer<PostPreviewEvent> {
        @SneakyThrows
        @Override
        public PostPreviewEvent deserialize(String topic, byte[] data) {
            return PostPreviewEvent.parseFrom(data);
        }
    }

    public class PreviewEventSerializer implements Serializer<PostPreviewEvent> {
        @Override
        public byte[] serialize(String topic, PostPreviewEvent data) {
            return data.toByteArray();
        }
    }
}
