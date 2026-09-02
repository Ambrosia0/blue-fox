package com.ambrosia.community_service.community.utils;

import com.ambrosia.community_service.community.model.entity.Community;
import com.ambrosia.community_service.kafka_events.CommunityCreate;
import com.ambrosia.community_service.kafka_events.CommunityDelete;
import com.ambrosia.community_service.kafka_events.CommunityEvent;
import com.ambrosia.community_service.kafka_events.CommunityUpdate;

import io.github.robsonkades.uuidv7.UUIDv7;

public final class CommunityEventFactory {
    public static CommunityEvent createOpration(Community community){
        var builder = CommunityCreate.newBuilder()
            .setId(community.getId())
            .setName(community.getSlug())
            .setOwnerId(community.getOwnerId().toString())
            .setIsPrivate(community.isPrivate());
        if(community.getAvatarId() != null)
            builder.setAvatarId(community.getAvatarId());
        return CommunityEvent.newBuilder()
            .setEventId(UUIDv7.randomUUIDString())
            .setCreate(builder.build())
            .build();
    }

    public static CommunityEvent updateOperation(Community community){
        var builder = CommunityUpdate.newBuilder()
            .setId(community.getId())
            .setName(community.getSlug())
            .setOwnerId(community.getOwnerId().toString())
            .setIsPrivate(community.isPrivate());
        if(community.getAvatarId() != null)
            builder.setAvatarId(community.getAvatarId());
        return CommunityEvent.newBuilder()
            .setEventId(UUIDv7.randomUUIDString())
            .setUpdate(builder.build())
            .build();
    }

    public static CommunityEvent deleteOperation(long id){
        var builder = CommunityDelete.newBuilder()
            .setId(id);
        return CommunityEvent.newBuilder()
            .setEventId(UUIDv7.randomUUIDString())
            .setDelete(builder.build())
            .build();
    }
}
