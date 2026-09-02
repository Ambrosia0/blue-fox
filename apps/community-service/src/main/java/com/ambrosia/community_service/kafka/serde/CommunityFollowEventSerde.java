package com.ambrosia.community_service.kafka.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.ambrosia.community_service.kafka_events.CommunityFollowEvent;

import lombok.SneakyThrows;

public class CommunityFollowEventSerde implements Serde<CommunityFollowEvent>{
    private final CommunityFollowEventDeserializer followMessageDeserializer = new CommunityFollowEventDeserializer();
    private final CommunityFollowEventSerializer followMessageSerializer = new CommunityFollowEventSerializer();

    @Override
    public Deserializer<CommunityFollowEvent> deserializer() {
        return followMessageDeserializer;
    }

    @Override
    public Serializer<CommunityFollowEvent> serializer() {
        return followMessageSerializer;
    }

    public class CommunityFollowEventDeserializer implements Deserializer<CommunityFollowEvent>{
        @SneakyThrows
        @Override
        public CommunityFollowEvent deserialize(String topic, byte[] data) {
            return CommunityFollowEvent.parseFrom(data);
        }
    }

    public class CommunityFollowEventSerializer implements Serializer<CommunityFollowEvent>{
        @Override
        public byte[] serialize(String topic, CommunityFollowEvent data) {
            return data.toByteArray();
        }
    }
}
