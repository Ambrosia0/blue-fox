package com.ambrosia.comment_service.attachment.utils;

import java.util.UUID;

public class AttachmentIdGenerator {
    public static String generateAttachmentId(Long commentId){
        return commentId + "_" + UUID.randomUUID();
    }
}
