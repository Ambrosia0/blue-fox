package org.ambrosia.notification_service.event.api.dto;

import com.ambrosia.content_service.grpc.User;

public record CommentPostEvent(
    Long commentId,
    Long postId,
    User user,
    String content,
    String attachmentId
) {}
