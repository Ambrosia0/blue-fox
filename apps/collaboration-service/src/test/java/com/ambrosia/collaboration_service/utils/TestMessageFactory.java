package com.ambrosia.collaboration_service.utils;

import java.nio.ByteBuffer;

import com.ambrosia.collaboration_service.model.entity.message.UserContextMessage;
import com.ambrosia.collaboration_service.model.entity.message.CollaborationMessage;
import com.ambrosia.collaboration_service.model.entity.message.Message;

public class TestMessageFactory {
    public static Message deserialize(ByteBuffer buffer){
        var code = MessageCodes.CODES.get(buffer.get());
        if(code == null)
            throw new IllegalArgumentException("Unknown operation code!");
        return switch(code){
            case SYNC_RESP -> UserContextMessage.deserialize(code, buffer);
            case SYNC -> UserContextMessage.deserialize(code, buffer);
            default -> CollaborationMessage.deserialize(code, buffer);
        };
    }
}
