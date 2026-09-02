package com.ambrosia.comment_service.utils.factory;

import java.util.UUID;

import com.ambrosia.comment_service.comment.model.entity.Comment;

public class CommentFactory {
    public static Comment create(Long postId, Long parentComment, UUID uuid){
        return Comment.builder()
            .content("TestContent")
            .userId(uuid)
            .postId(postId)
            .parentCommentId(parentComment)
            .build();
    }
    
    public static Comment create(Long postId, Long parentComment){
        return Comment.builder()
            .content("TestContent")
            .userId(UUID.randomUUID())
            .postId(postId)
            .parentCommentId(parentComment)
            .build();
    }
}
