package com.ambrosia.comment_service.comment.api.dto.response;

import java.time.Instant;

import com.ambrosia.comment_service.user.model.dto.UserResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

public record CommentData(
    Long id,
    Long postId,
    UserResponse user,
    String content,
    int likeCount,

    Long parentComment,
    
    Instant createdAt,
    int numberOfChildren,

    Boolean isLiked,

    String attachmentUrl,
    
    @JsonIgnore
    Long version
){}