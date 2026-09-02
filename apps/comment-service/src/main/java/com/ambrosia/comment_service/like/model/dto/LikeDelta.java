package com.ambrosia.comment_service.like.model.dto;

public record LikeDelta(
    long postId,
    long commentId,
    long delta
) {
    public static LikeDelta create(long postId, long commentId, long delta){
        return new LikeDelta(postId, commentId, delta);
    }
}
