package com.ambrosia.collaboration_service.model.entity.message;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.springframework.web.socket.BinaryMessage;

import com.ambrosia.collaboration_service.utils.MessageCodes;

/**
 * @param code operation code
 * @param userId user associated with message, meaning depends on the message code
 * @param payload byte payload
 */
public record UserContextMessage(
    MessageCodes code,
    UUID userId,
    byte[] payload
)
implements Message {
    public BinaryMessage serialize(){
        return new BinaryMessage(ByteBuffer.allocate(17 + payload.length)
            .put(code.getCode())
            .putLong(userId.getMostSignificantBits())
            .putLong(userId.getLeastSignificantBits())
            .put(payload)
            .array()
        );
    }

    public static UserContextMessage deserialize(MessageCodes code, ByteBuffer buffer){
        var id = new UUID(buffer.getLong(), buffer.getLong());
        var payload = new byte[buffer.remaining()];
        buffer.put(payload);
        return new UserContextMessage(
            code,
            id,
            payload
        );
    }

    public static UserContextMessage create(MessageCodes code, UUID userId, byte[] payload){
        return new UserContextMessage(
            code,
            userId,
            payload
        );
    }
}
