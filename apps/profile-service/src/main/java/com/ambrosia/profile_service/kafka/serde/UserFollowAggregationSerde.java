package com.ambrosia.profile_service.kafka.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.ambrosia.profile_service.kafka_events.UserFollowAggregation;

import lombok.SneakyThrows;

public class UserFollowAggregationSerde implements Serde<UserFollowAggregation>{
    private final UserFollowAggregationSerializer serializer = new UserFollowAggregationSerializer();
    private final UserFollowAggregationDeserializer deserializer = new UserFollowAggregationDeserializer();

    @Override
    public Deserializer<UserFollowAggregation> deserializer() {
        return deserializer;
    }

    @Override
    public Serializer<UserFollowAggregation> serializer() {
        return serializer;
    }

    public class UserFollowAggregationSerializer implements Serializer<UserFollowAggregation>{
        @Override
        public byte[] serialize(String topic, UserFollowAggregation data) {
            return data.toByteArray();
        }
    }

    public class UserFollowAggregationDeserializer implements Deserializer<UserFollowAggregation>{
        @SneakyThrows
        @Override
        public UserFollowAggregation deserialize(String topic, byte[] data) {
            return UserFollowAggregation.parseFrom(data);
        }
    }
}
