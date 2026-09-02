package com.ambrosia.community_service.kafka.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.ambrosia.community_service.kafka_events.CommunityFollowAggregation;

import lombok.SneakyThrows;

public class CommunityFollowAggregationSerde implements Serde<CommunityFollowAggregation>{
    private final CommunityFollowAggregationDeserializer deserializer = new CommunityFollowAggregationDeserializer();
    private final CommunityFollowAggregationSerializer serializer = new CommunityFollowAggregationSerializer();
    
    @Override
    public Deserializer<CommunityFollowAggregation> deserializer() {
        return deserializer;
    }

    @Override
    public Serializer<CommunityFollowAggregation> serializer() {
        return serializer;
    }

    public class CommunityFollowAggregationDeserializer implements Deserializer<CommunityFollowAggregation>{
        @SneakyThrows
        @Override
        public CommunityFollowAggregation deserialize(String topic, byte[] data) {
            return CommunityFollowAggregation.parseFrom(data);
        }
    }

    public class CommunityFollowAggregationSerializer implements Serializer<CommunityFollowAggregation>{
        @Override
        public byte[] serialize(String topic, CommunityFollowAggregation data) {
            return data.toByteArray();
        }
    }
}
