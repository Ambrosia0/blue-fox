package com.ambrosia.content_service.kafka.utils;

import com.ambrosia.content_service.kafka_events.PostCreated;
import com.ambrosia.content_service.kafka_events.PostDeleted;
import com.ambrosia.content_service.kafka_events.PostEvent;
import com.ambrosia.content_service.post.model.DeletionProjection;
import com.ambrosia.content_service.post.model.entity.Post;

import io.github.robsonkades.uuidv7.UUIDv7;

public class PostMessageFactory {
    public static PostEvent createOperation(Post post){
        var builder = PostCreated.newBuilder()
            .setId(post.getId())
            .setTitle(post.getTitle())
            .setAuthorId(post.getAuthorId().toString())
            .setPreview(post.getPreview());
        if(post.getPublishedAt() != null)
            builder.setPublishedAt(post.getPublishedAt().toEpochMilli());
        if(post.getCommunityId() != null)
            builder.setCommunityId(post.getCommunityId());
        builder.setAuthorId(post.getAuthorId().toString());
        return PostEvent.newBuilder()
            .setEventId(UUIDv7.randomUUIDString())
            .setCreated(builder.build())
            .build();
    }

    public static PostEvent deleteOperation(DeletionProjection deletionProjection){
        var builder = PostDeleted.newBuilder()
            .setId(deletionProjection.id());
        if(deletionProjection.communityId() != null)
            builder.setCommunityId(deletionProjection.communityId());
        return PostEvent.newBuilder()
            .setEventId(UUIDv7.randomUUIDString())
            .setDeleted(builder.build())
            .build();
    }
    
}
