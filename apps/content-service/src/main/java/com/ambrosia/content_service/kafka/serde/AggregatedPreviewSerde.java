package com.ambrosia.content_service.kafka.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.ambrosia.content_service.kafka_events.AggregatedPreviewEvent;

import lombok.SneakyThrows;

public class AggregatedPreviewSerde implements Serde<AggregatedPreviewEvent>{
    private final AggregatedPreviewDeserializer deserializer = new AggregatedPreviewDeserializer();
    private final AggregatedPreviewSerializer serializer = new AggregatedPreviewSerializer();

    @Override
    public Deserializer<AggregatedPreviewEvent> deserializer() {
        return deserializer;
    }

    @Override
    public Serializer<AggregatedPreviewEvent> serializer() {
        return serializer;
    }

    public class AggregatedPreviewDeserializer implements Deserializer<AggregatedPreviewEvent>{
        @SneakyThrows
        @Override
        public AggregatedPreviewEvent deserialize(String topic, byte[] data) {
            return AggregatedPreviewEvent.parseFrom(data);
        }
    }

    public class AggregatedPreviewSerializer implements Serializer<AggregatedPreviewEvent>{
        @Override
        public byte[] serialize(String topic, AggregatedPreviewEvent data) {
            return data.toByteArray();
        }
    }
}
