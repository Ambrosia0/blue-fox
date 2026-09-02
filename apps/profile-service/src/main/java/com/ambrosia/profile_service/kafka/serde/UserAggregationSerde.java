package com.ambrosia.profile_service.kafka.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.ambrosia.profile_service.kafka_events.UserAggregation;

import lombok.SneakyThrows;

public class UserAggregationSerde implements Serde<UserAggregation>{
    private final UserAggregationSerializer serializer = new UserAggregationSerializer();
    private final UserAggregationDeserializer deserializer = new UserAggregationDeserializer();

    @Override
    public Deserializer<UserAggregation> deserializer() {
        return deserializer;
    }

    @Override
    public Serializer<UserAggregation> serializer() {
        return serializer;
    }

    public class UserAggregationSerializer implements Serializer<UserAggregation>{
        @Override
        public byte[] serialize(String topic, UserAggregation data) {
            return data.toByteArray();
        }
    }

    public class UserAggregationDeserializer implements Deserializer<UserAggregation>{
        @SneakyThrows
        @Override
        public UserAggregation deserialize(String topic, byte[] data) {
            return UserAggregation.parseFrom(data);
        }
    }
}
