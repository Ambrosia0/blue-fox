package com.ambrosia.collaboration_service.model.entity.message;

import java.nio.ByteBuffer;

import org.springframework.web.socket.BinaryMessage;

import com.ambrosia.collaboration_service.utils.MessageCodes;

public record CollaborationMessage(
    MessageCodes code,
    byte[] payload
) implements Message{
    
    public BinaryMessage serialize(){
        return new BinaryMessage(ByteBuffer
            .allocate(1 + payload.length)
            .put(code.getCode())
            .put(payload)
            .array()
        );
    }

    public static CollaborationMessage deserialize(MessageCodes codes, ByteBuffer buffer){
        var bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return new CollaborationMessage(
            codes, 
            bytes
        );
    }

    public static CollaborationMessage create(MessageCodes code, byte[] payload){
        return new CollaborationMessage(
            code,
            payload
        );
    }
}
