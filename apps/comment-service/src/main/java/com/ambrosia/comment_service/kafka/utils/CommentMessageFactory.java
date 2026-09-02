package com.ambrosia.comment_service.kafka.utils;

import com.ambrosia.comment_service.comment.model.dto.response.CreateCommentResponse;
import com.ambrosia.comment_service.comment.model.entity.Comment;
import com.ambrosia.comment_service.kafka_events.CommentCreated;
import com.ambrosia.comment_service.kafka_events.CommentDeleted;
import com.ambrosia.comment_service.kafka_events.CommentEvent;

import jakarta.annotation.Nullable;

public class CommentMessageFactory {
    public static CommentEvent createOperation(Comment comment, @Nullable String attachmentUrl){
        var builder = CommentCreated.newBuilder();
        builder.setId(comment.getId())
            .setPostId(comment.getPostId())
            .setUserId(comment.getUserId().toString())
            .setContent(comment.getContent())
            .setCreatedAt(comment.getCreatedAt().toEpochMilli());
        if(comment.getParentCommentId() != null)
            builder.setParentComent(comment.getParentCommentId());
        if(attachmentUrl != null)
            builder.setAttachmentUrl(attachmentUrl);
        return CommentEvent.newBuilder()
            .setCreated(builder.build())
            .build();
    }

    public static CommentEvent createOperation(CreateCommentResponse response){
        var builder = CommentCreated.newBuilder();
        builder.setId(response.getId())
            .setPostId(response.getPostId())
            .setUserId(response.getUserId().toString())
            .setContent(response.getContent())
            .setCreatedAt(response.getCreatedAt().toEpochMilli());
        if(response.getParentComment() != null)
            builder.setParentComent(response.getParentComment());
        if(response.getAttachmentId() != null)
            builder.setAttachmentUrl(response.getAttachmentId());
        return CommentEvent.newBuilder()
            .setCreated(builder.build())
            .build();
    }

    public static CommentEvent deleteOperation(long commentId){
        var deleted = CommentDeleted.newBuilder()
            .setId(commentId)
            .build();
        return CommentEvent.newBuilder()
            .setDeleted(deleted)
            .build();
    }
}
