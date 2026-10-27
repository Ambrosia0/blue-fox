package com.ambrosia.comment_service.infrastructure.kafka.utils;

import com.ambrosia.comment_service.comment.api.dto.response.CommentData;
import com.ambrosia.comment_service.comment.domain.entity.DeletedComment;
import com.ambrosia.comment_service.kafka_events.CommentCreated;
import com.ambrosia.comment_service.kafka_events.CommentDeleted;
import com.ambrosia.comment_service.kafka_events.CommentEvent;
import com.ambrosia.comment_service.kafka_events.User;
import com.ambrosia.comment_service.user.model.dto.UserResponse;
import com.github.f4b6a3.uuid.UuidCreator;


public class CommentMessageFactory {
    public static CommentEvent createOperation(CommentData comment){
        var builder = CommentCreated.newBuilder()
            .setUser(toUser(comment.user()))
            .setContent(comment.content())
            .setCreatedAt(comment.createdAt().toEpochMilli());
        if(comment.parentComment() != null)
            builder.setParentComment(comment.parentComment());
        if(comment.attachmentUrl() != null)
            builder.setAttachmentUrl(comment.attachmentUrl());
        return CommentEvent.newBuilder()
            .setPostId(comment.postId())
            .setCommentId(comment.id())
            .setVersion(comment.version())
            .setEventId(UuidCreator.getTimeOrderedEpoch().toString())
            .setCreated(builder.build())
            .build();
    }

    public static CommentEvent deleteOperation(DeletedComment deletedComment){
        return CommentEvent.newBuilder()
            .setCommentId(deletedComment.commentId())
            .setEventId(UuidCreator.getTimeOrderedEpoch().toString())
            .setPostId(deletedComment.postId())
            .setVersion(deletedComment.version())
            .setDeleted(CommentDeleted.newBuilder()
                .build())
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
