package com.ambrosia.comment_service.infrastructure.kafka.utils;

import com.ambrosia.comment_service.comment.api.dto.response.CommentData;
import com.ambrosia.comment_service.kafka_events.CommentCreated;
import com.ambrosia.comment_service.kafka_events.CommentDeleted;
import com.ambrosia.comment_service.kafka_events.CommentEvent;
import com.ambrosia.comment_service.kafka_events.User;
import com.ambrosia.comment_service.user.model.dto.UserResponse;

import io.github.robsonkades.uuidv7.UUIDv7;

public class CommentMessageFactory {
    public static CommentEvent createOperation(CommentData comment){
        var builder = CommentCreated.newBuilder()
            .setPostId(comment.postId())
            .setUser(toUser(comment.user()))
            .setContent(comment.content())
            .setCreatedAt(comment.createdAt().toEpochMilli());
        if(comment.parentComment() != null)
            builder.setParentComent(comment.parentComment());
        if(comment.attachmentUrl() != null)
            builder.setAttachmentUrl(comment.attachmentUrl());
        return CommentEvent.newBuilder()
            .setCommentId(comment.id())
            .setEventId(UUIDv7.randomUUIDString())
            .setCreated(builder.build())
            .build();
    }

    public static CommentEvent deleteOperation(long commentId){
        var deleted = CommentDeleted.newBuilder()
            .build();
        return CommentEvent.newBuilder()
            .setCommentId(commentId)
            .setEventId(UUIDv7.randomUUIDString())
            .setDeleted(deleted)
            .build();
    }

    private static User toUser(UserResponse userResponse){
        var builder = User.newBuilder()
            .setId(userResponse.id().toString())
            .setUsername(userResponse.username())
            .setLastName(userResponse.lastName())
            .setFirstName(userResponse.firstName());
        if(userResponse.avatarId() != null)
            builder.setAvatarId(userResponse.avatarId());
        return builder.build();
    }
}
