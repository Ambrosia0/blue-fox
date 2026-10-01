package com.ambrosia.comment_service.comment.service.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.ambrosia.comment_service.comment.model.dto.request.CreateComment;
import com.ambrosia.comment_service.comment.model.entity.Comment;

@Component 
public class CommentMapper {
    public Comment toEntity(UUID userId, CreateComment createComment){
        return Comment.builder()
            .postId(createComment.postId())
            .userId(userId)
            .content(createComment.content())
            .parentCommentId(createComment.parentComment())
            .build();
    }
}
