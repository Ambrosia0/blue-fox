package com.ambrosia.profile_service.kafka.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.ambrosia.comment_service.kafka_events.CommentEvent;

import lombok.SneakyThrows;

public class CommentEventSerde implements Serde<CommentEvent>{
    private final CommentMessageSerializer serializer = new CommentMessageSerializer();
    private final CommentMessageDeserializer deserializer = new CommentMessageDeserializer();

    @Override
    public Deserializer<CommentEvent> deserializer() {
        return deserializer;
    }

    @Override
    public Serializer<CommentEvent> serializer() {
        return serializer;
    }


    public class CommentMessageDeserializer implements Deserializer<CommentEvent>{
        @SneakyThrows
        @Override
        public CommentEvent deserialize(String topic, byte[] data) {
            return CommentEvent.parseFrom(data);
        }
    }
    
    public class CommentMessageSerializer implements Serializer<CommentEvent>{
        @Override
        public byte[] serialize(String topic, CommentEvent data) {
            return data.toByteArray();
        }
    }

}
