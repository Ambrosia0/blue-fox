package com.ambrosia.collaboration_service.utils.serializers;

import java.nio.ByteBuffer;

import com.ambrosia.collaboration_service.utils.NotificationCodes;
import com.ambrosia.content_service.kafka_events.collaboration.CollaborationAttachmentDelete;

public class AttachmentDeleteSerializer implements EventSerializer<CollaborationAttachmentDelete>{
    @Override
    public byte[] serialize(CollaborationAttachmentDelete obj) {
        var bytes = obj.getAttachmentId().getBytes();
        var buf = ByteBuffer.allocate(1 + bytes.length);
        return buf
            .put(NotificationCodes.ATTACHMENT_DELETE.getCode())
            .put(bytes)
            .array();
    }

    @Override
    public Class<CollaborationAttachmentDelete> type() {
        return CollaborationAttachmentDelete.class;
    }
}
