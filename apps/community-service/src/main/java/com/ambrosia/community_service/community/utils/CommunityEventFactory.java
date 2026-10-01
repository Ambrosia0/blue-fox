package com.ambrosia.community_service.community.utils;

import java.util.List;
import java.util.stream.Collectors;

import com.ambrosia.community_service.community.model.entity.Community;
import com.ambrosia.community_service.community.model.entity.ScopeLink;
import com.ambrosia.community_service.kafka_events.CommunityCreate;
import com.ambrosia.community_service.kafka_events.CommunityDelete;
import com.ambrosia.community_service.kafka_events.CommunityEvent;
import com.ambrosia.community_service.kafka_events.CommunityUpdate;
import com.ambrosia.community_service.kafka_events.ModeratorPermissions;
import com.ambrosia.community_service.kafka_events.Permission;

import io.github.robsonkades.uuidv7.UUIDv7;

public final class CommunityEventFactory {
    public static CommunityEvent createOpration(Community community){
        var builder = CommunityCreate.newBuilder()
            .setName(community.getSlug())
            .setOwnerId(community.getOwnerId().toString())
            .putAllCommunityPermissions(community.getScopes().stream()
                .collect(Collectors.groupingBy(t -> t.getUserId()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                    k -> k.getKey().toString(),
                    pair -> toPermission(pair.getValue()) 
                ))
            )
            .setIsPrivate(community.isPrivate())
            .setSlug(community.getSlug());
        if(community.getAvatarId() != null)
            builder.setAvatarId(community.getAvatarId());
        return CommunityEvent.newBuilder()
            .setId(community.getId())
            .setEventId(UUIDv7.randomUUIDString())
            .setCreate(builder.build())
            .build();
    }

    public static CommunityEvent updateOperation(Community community){
        var builder = CommunityUpdate.newBuilder()
            .setName(community.getSlug())
            .setOwnerId(community.getOwnerId().toString())
            .putAllCommunityPermissions(community.getScopes().stream()
                .collect(Collectors.groupingBy(t -> t.getUserId()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                    k -> k.getKey().toString(),
                    pair -> toPermission(pair.getValue()) 
                ))
            )
            .setIsPrivate(community.isPrivate())
            .setSlug(community.getSlug());
        if(community.getAvatarId() != null)
            builder.setAvatarId(community.getAvatarId());
        return CommunityEvent.newBuilder()
            .setId(community.getId())
            .setEventId(UUIDv7.randomUUIDString())
            .setUpdate(builder.build())
            .build();
    }

    public static CommunityEvent deleteOperation(long id){
        var deleted = CommunityDelete.newBuilder().build();
        return CommunityEvent.newBuilder()
            .setId(id)
            .setEventId(UUIDv7.randomUUIDString())
            .setDelete(deleted)
            .build();
    }

    private static ModeratorPermissions toPermission(List<ScopeLink> scopes){
        var builder = ModeratorPermissions.newBuilder();
        builder.addAllPermissions(scopes.stream()
            .map(t -> toPermission(ScopeEnum.fromId(t.getScopeId())))
            .toList()
        );
        return builder.build();
    }

    private static Permission toPermission(ScopeEnum scopeEnum){
        return switch (scopeEnum) {
            case COMMENT_DELETE -> Permission.COMMENT_DELETE;
            case FOLLOW_MANAGE -> Permission.FOLLOW_MANAGE;
            case POST_DELETE -> Permission.POST_DELETE;
            case USER_BAN -> Permission.USER_BAN;
            case USER_UNBAN -> Permission.USER_UNBAN;
            default -> Permission.UNRECOGNIZED;
        };
    }
}
