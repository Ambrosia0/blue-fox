package com.ambrosia.profile_service.kafka.serde;

import org.ambrosia.notification_service.kafka_events.ActivityEvent;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import lombok.SneakyThrows;

public class ActivityEventSerde implements Serde<ActivityEvent>{
    private final ActivityEventDeserializer deserializer = new ActivityEventDeserializer();
    private final ActivityEventSerializer serializer = new ActivityEventSerializer();

    @Override
    public Deserializer<ActivityEvent> deserializer() {
        return deserializer;
    }

    @Override
    public Serializer<ActivityEvent> serializer() {
        return serializer;
    }

    public class ActivityEventDeserializer implements Deserializer<ActivityEvent>{
        @SneakyThrows
        @Override
        public ActivityEvent deserialize(String topic, byte[] data) {
            return ActivityEvent.parseFrom(data);
        }
    }

    public class ActivityEventSerializer implements Serializer<ActivityEvent>{
        @Override
        public byte[] serialize(String topic, ActivityEvent data) {
            return data.toByteArray();
        }
    }
    
}
