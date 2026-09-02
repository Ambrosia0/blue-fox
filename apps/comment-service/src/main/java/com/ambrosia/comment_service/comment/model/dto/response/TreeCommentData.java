package com.ambrosia.comment_service.comment.model.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public record TreeCommentData(
    Long id,
    Long postId,
    UUID userId,
    String content,
    int likeCount,

    Long parentComment,
    
    Instant createdAt,
    int numberOfChildren,

    @JsonInclude(value = Include.NON_NULL)
    Boolean isLiked,

    @JsonInclude(value = Include.NON_NULL)
    Float score,

    String attachmentUrl
){}