package com.ambrosia.collaboration_service.utils.serializers;

import java.nio.ByteBuffer;

import org.springframework.stereotype.Component;

import com.ambrosia.collaboration_service.utils.NotificationCodes;
import com.ambrosia.content_service.kafka_events.collaboration.CollaborationAttachmentCreate;

@Component 
public class AttachmentCreateSerializer implements EventSerializer<CollaborationAttachmentCreate>{
    @Override
    public byte[] serialize(CollaborationAttachmentCreate obj) {
        var bytes = obj.getAttachmentId().getBytes();
        var buf = ByteBuffer.allocate(1 + bytes.length);
        return buf
            .put(NotificationCodes.ATTACHMENT_CREATE.getCode())
            .put(bytes)
            .array();
    }

    @Override
    public Class<CollaborationAttachmentCreate> type() {
        return CollaborationAttachmentCreate.class;
    }
}
