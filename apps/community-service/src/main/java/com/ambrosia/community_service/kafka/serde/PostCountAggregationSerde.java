package com.ambrosia.community_service.kafka.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.ambrosia.community_service.kafka_events.PostCountAggregation;

public class PostCountAggregationSerde implements Serde<PostCountAggregation>{
    private final static Serializer<PostCountAggregation> SERIALIZER = (topic, data) -> {
        return data.toByteArray();
    };

    private final static Deserializer<PostCountAggregation> DESERIALIZER = (topic, data) -> {
        try {
            return PostCountAggregation.parseFrom(data);
        } catch (Exception e) {
            throw new RuntimeException("Invalid body format!");
        }
    };

    @Override
    public Deserializer<PostCountAggregation> deserializer() {
        return DESERIALIZER;
    }

    @Override
    public Serializer<PostCountAggregation> serializer() {
        return SERIALIZER;
    }
}
