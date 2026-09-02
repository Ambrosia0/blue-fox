package com.ambrosia.comment_service.comment.model.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public record RootCommentData(
    long commentId,
    long postId,
    UUID userId,
    String content,
    Integer likeCount,
    int numberOfChildren,
    
    Instant createdAt,

    @JsonInclude(value = Include.NON_NULL)
    Boolean isLiked,

    String attachmentUrl
){}