package com.ambrosia.content_service.attachment.utils;

import java.util.UUID;

public class AttachmentIdGenerator {
    public static String generateAttachmentId(long postId){
        return
            postId+
            "_"+
            UUID.randomUUID();
    }
}
