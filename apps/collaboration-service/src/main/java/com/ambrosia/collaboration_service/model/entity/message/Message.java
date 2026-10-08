package com.ambrosia.collaboration_service.model.entity.message;

import java.nio.ByteBuffer;

import org.springframework.web.socket.BinaryMessage;

import com.ambrosia.collaboration_service.utils.MessageCodes;

public sealed interface Message permits CollaborationMessage, UserContextMessage {
    MessageCodes code();
    byte[] payload();
    BinaryMessage serialize();

    public static Message deserialize(ByteBuffer buffer){
        var rawCode = buffer.get();
        var code = MessageCodes.CODES.get(rawCode);
        if(code == null)
            throw new IllegalArgumentException("Unknown operation code! "+rawCode);
        return switch(code){
            case SYNC_RESP -> UserContextMessage.deserialize(code, buffer);
            case SYNC -> CollaborationMessage.create(code, new byte[0]);
            case USER_JOIN, USER_LEAVE, ABORT, STATE, NOTIFICATION -> 
                throw new IllegalArgumentException("Server-only operation code: " + code); // server-codes
            default -> CollaborationMessage.deserialize(code, buffer);
        };
    }
}
