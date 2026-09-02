package com.ambrosia.profile_service.kafka.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.ambrosia.content_service.kafka_events.PostEvent;

import lombok.SneakyThrows;

public class PostEventSerde implements Serde<PostEvent>{
    private final PostMessageDeserializer deserializer = new PostMessageDeserializer();

    private final PostMessageSerializer serializer = new PostMessageSerializer();

    @Override
    public Deserializer<PostEvent> deserializer() {
        return deserializer;
    }

    @Override
    public Serializer<PostEvent> serializer() {
        return serializer;
    }

    public class PostMessageDeserializer implements Deserializer<PostEvent>{
        @SneakyThrows
        @Override
        public PostEvent deserialize(String topic, byte[] data) {
            return PostEvent.parseFrom(data);
        }
    }


    public class PostMessageSerializer implements Serializer<PostEvent>{
        @Override
        public byte[] serialize(String topic, PostEvent data) {
            return data.toByteArray();
        }
    }

}
