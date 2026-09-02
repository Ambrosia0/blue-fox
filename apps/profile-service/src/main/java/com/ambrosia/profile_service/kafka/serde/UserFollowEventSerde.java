package com.ambrosia.profile_service.kafka.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.ambrosia.content_service.kafka_events.UserFollowEvent;

import lombok.SneakyThrows;

public class UserFollowEventSerde implements Serde<UserFollowEvent>{
    private final UserFollowEventDeserializer followMessageDeserializer = new UserFollowEventDeserializer();
    private final UserFollowEventSerializer followMessageSerializer = new UserFollowEventSerializer();

    @Override
    public Deserializer<UserFollowEvent> deserializer() {
        return followMessageDeserializer;
    }

    @Override
    public Serializer<UserFollowEvent> serializer() {
        return followMessageSerializer;
    }

    public class UserFollowEventDeserializer implements Deserializer<UserFollowEvent>{
        @SneakyThrows
        @Override
        public UserFollowEvent deserialize(String topic, byte[] data) {
            return UserFollowEvent.parseFrom(data);
        }
    }

    public class UserFollowEventSerializer implements Serializer<UserFollowEvent>{
        @Override
        public byte[] serialize(String topic, UserFollowEvent data) {
            return data.toByteArray();
        }
    }
}
