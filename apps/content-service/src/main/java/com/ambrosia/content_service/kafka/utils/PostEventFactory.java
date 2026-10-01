package com.ambrosia.content_service.kafka.utils;

import com.ambrosia.content_service.grpc.Community;
import com.ambrosia.content_service.grpc.ReplyTo;
import com.ambrosia.content_service.grpc.User;
import com.ambrosia.content_service.kafka_events.PostCreated;
import com.ambrosia.content_service.kafka_events.PostDeleted;
import com.ambrosia.content_service.kafka_events.PostEvent;
import com.ambrosia.content_service.kafka_events.PostUpdated;
import com.ambrosia.content_service.post.model.DeletionProjection;
import com.ambrosia.content_service.post.model.dto.response.CommunityResponse;
import com.ambrosia.content_service.post.model.dto.response.PostResponse;
import com.ambrosia.content_service.post.model.dto.response.PostViewResponse;
import com.ambrosia.content_service.post.model.dto.response.UserResponse;
import com.ambrosia.content_service.post.model.entity.Post;

import io.github.robsonkades.uuidv7.UUIDv7;

public class PostEventFactory {
    public static PostEvent createOperation(PostViewResponse post){
        var builder = PostCreated.newBuilder()
            .setTitle(post.title())
            .setAuthor(toUser(post.user()))
            .setPreview(post.preview());
        if(post.publishedAt() != null)
            builder.setPublishedAt(post.publishedAt().toEpochMilli());
        if(post.community() != null)
            builder.setCommunity(toCommunity(post.community()));
        if(post.response() != null)
            builder.setReplyTo(toReplyTo(post.response()));
        return PostEvent.newBuilder()
            .setPostId(post.id())
            .setEventId(UUIDv7.randomUUIDString())
            .setCreated(builder.build())
            .build();
    }

    public static PostEvent updateOperation(Post post){
        var builder = PostUpdated.newBuilder()
            .setIsPublished(post.isPublished());
        if(post.getCommunityId() != null)
            builder.setCommunityId(post.getCommunityId().getId());
        return PostEvent.newBuilder()
            .setPostId(post.getId())
            .setEventId(UUIDv7.randomUUIDString())
            .setUpdated(builder.build())
            .build();
    }

    public static PostEvent deleteOperation(DeletionProjection deletionProjection){
        var builder = PostDeleted.newBuilder();
        if(deletionProjection.communityId() != null)
            builder.setCommunityId(deletionProjection.communityId());
        return PostEvent.newBuilder()
            .setPostId(deletionProjection.id())
            .setEventId(UUIDv7.randomUUIDString())
            .setDeleted(builder.build())
            .build();
    }

    private static ReplyTo toReplyTo(PostResponse postResponse){
        return ReplyTo.newBuilder()
            .setId(postResponse.id())
            .setTitle(postResponse.title())
            .build();
    }
    
    private static User toUser(UserResponse userResponse){
        var builder = User.newBuilder()
            .setId(userResponse.id().toString())
            .setUsername(userResponse.username())
            .setFirstName(userResponse.firstName())
            .setLastName(userResponse.lastName());
        if(userResponse.avatarId() != null)
            builder.setAvatarId(userResponse.avatarId());
        return builder.build();
    }

    private static Community toCommunity(CommunityResponse communityResponse){
        var builder = Community.newBuilder()
            .setId(communityResponse.id())
            .setName(communityResponse.name())
            .setSlug(communityResponse.slug())
            .setIsPrivate(communityResponse.isPrivate());
        if(communityResponse.avatarId() != null)
            builder.setAvatarId(communityResponse.avatarId());
        return builder.build();
    }
}
