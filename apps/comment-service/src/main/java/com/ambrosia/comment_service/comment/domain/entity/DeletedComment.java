package com.ambrosia.comment_service.comment.domain.entity;

public record DeletedComment(
    Long commentId,
    Long postId,
    Long version
) {}
