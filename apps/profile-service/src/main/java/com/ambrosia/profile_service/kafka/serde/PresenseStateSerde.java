package com.ambrosia.profile_service.kafka.serde;

import java.nio.ByteBuffer;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.ambrosia.profile_service.kafka.utils.PresenseState;

import lombok.SneakyThrows;

public class PresenseStateSerde implements Serde<PresenseState>{
    private final PresenseStateDeserializer deserializer = new PresenseStateDeserializer();
    private final PresenseStateSerializer serializer = new PresenseStateSerializer();

    @Override
    public Deserializer<PresenseState> deserializer() {
        return deserializer;
    }

    @Override
    public Serializer<PresenseState> serializer() {
        return serializer;
    }

    public class PresenseStateDeserializer implements Deserializer<PresenseState>{
        @SneakyThrows
        @Override
        public PresenseState deserialize(String topic, byte[] data) {
            var buf = ByteBuffer.wrap(data);
            return new PresenseState(
                buf.get(),
                buf.getLong()
            );
        }
    }

    public class PresenseStateSerializer implements Serializer<PresenseState>{
        @Override
        public byte[] serialize(String topic, PresenseState data) {
            var buf = ByteBuffer.allocate(9);
            buf.put(data.delta);
            buf.putLong(data.timestamp);
            return buf.array();
        }
    }
}
