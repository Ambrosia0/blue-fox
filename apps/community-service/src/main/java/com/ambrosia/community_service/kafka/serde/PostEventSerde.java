package com.ambrosia.community_service.kafka.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.ambrosia.content_service.kafka_events.PostEvent;

public class PostEventSerde implements Serde<PostEvent>{
    private static Deserializer<PostEvent> DESERIALIZER = (topic, data) -> {
        try {
            return PostEvent.parseFrom(data);
        } catch (Exception e) {
            throw new RuntimeException("Wrong message body!");
        }
    };

    private static Serializer<PostEvent> SERIALIZER = (topic, data) -> {
        return data.toByteArray();
    };
    

    @Override
    public Deserializer<PostEvent> deserializer() {
        return DESERIALIZER;
    }

    @Override
    public Serializer<PostEvent> serializer() {
        return SERIALIZER;
    }
}
