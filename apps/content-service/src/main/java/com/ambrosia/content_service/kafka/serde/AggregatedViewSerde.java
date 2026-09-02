package com.ambrosia.content_service.kafka.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.ambrosia.content_service.kafka_events.AggregatedViewEvent;

import lombok.SneakyThrows;

public class AggregatedViewSerde implements Serde<AggregatedViewEvent>{
    private final AggregatedViewDeserializer deserializer = new AggregatedViewDeserializer();
    private final AggregatedViewSerializer serializer = new AggregatedViewSerializer();

    @Override
    public Deserializer<AggregatedViewEvent> deserializer() {
        return deserializer;
    }

    @Override
    public Serializer<AggregatedViewEvent> serializer() {
        return serializer;
    }

    public class AggregatedViewDeserializer implements Deserializer<AggregatedViewEvent>{
        @SneakyThrows
        @Override
        public AggregatedViewEvent deserialize(String topic, byte[] data) {
            return AggregatedViewEvent.parseFrom(data);
        }
    }

    public class AggregatedViewSerializer implements Serializer<AggregatedViewEvent>{
        @Override
        public byte[] serialize(String topic, AggregatedViewEvent data) {
            return data.toByteArray();
        }
    }
}
