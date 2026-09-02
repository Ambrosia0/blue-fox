package com.ambrosia.profile_service.kafka.serde;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class CompactUuidSerde implements Serde<UUID>{
    private final static Deserializer<UUID> DESERIALIZER = (topic, data) ->{
        if(data == null)
            return null;
        var buf = ByteBuffer.wrap(data);
        return new UUID(buf.getLong(), buf.getLong());
    };
    private final static Serializer<UUID> SERIALIZER = (topic, data) -> {
        if(data == null)
            return null;
        var buf = ByteBuffer.allocate(16)
            .putLong(data.getMostSignificantBits())
            .putLong(data.getLeastSignificantBits());
        return buf.array();
    };

    @Override
    public Deserializer<UUID> deserializer() {
        return DESERIALIZER;
    }

    @Override
    public Serializer<UUID> serializer() {
        return SERIALIZER;
    }
}
