package com.ambrosia.collaboration_service.utils;

import java.nio.ByteBuffer;
import java.util.UUID;

public class CompactUUIDSerializer {
    public final static int UUID_SIZE = 16;

    public static byte[] serializer(UUID uuid){
        return ByteBuffer.allocate(16)
            .putLong(uuid.getMostSignificantBits())
            .putLong(uuid.getLeastSignificantBits())
            .array();
    }

    public static ByteBuffer serializer(ByteBuffer buf, UUID uuid){
        return buf
            .putLong(uuid.getMostSignificantBits())
            .putLong(uuid.getLeastSignificantBits());
    }
}
