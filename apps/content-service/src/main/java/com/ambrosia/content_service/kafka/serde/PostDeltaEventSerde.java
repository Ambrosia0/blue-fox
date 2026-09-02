package com.ambrosia.content_service.kafka.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.ambrosia.content_service.kafka_events.PostDelta;


public class PostDeltaEventSerde implements Serde<PostDelta>{
    private final static Deserializer<PostDelta> DESERIALIZR = (topic, data) -> {
        try {
            return PostDelta.parseFrom(data);
        } catch (Exception e) {
            throw new RuntimeException("Invalid body!");
        }
    };

    private final static Serializer<PostDelta> SERIALIZER = (topic, data) -> {
        return data.toByteArray();
    };
    
    @Override
    public Deserializer<PostDelta> deserializer() {
        return DESERIALIZR;
    }

    @Override
    public Serializer<PostDelta> serializer() {
        return SERIALIZER;
    }
}
