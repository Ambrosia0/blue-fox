package com.ambrosia.content_service.community.service.mapper;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ambrosia.community_service.kafka_events.CommunityCreate;
import com.ambrosia.community_service.kafka_events.CommunityUpdate;
import com.ambrosia.community_service.kafka_events.ModeratorPermissions;
import com.ambrosia.community_service.kafka_events.Permission;
import com.ambrosia.content_service.community.model.entity.CommunityPermission;
import com.ambrosia.content_service.community.model.entity.CommunityProjection;

@Component 
public class CommunityMapper {
    public CommunityProjection toEntity(Long communityId, CommunityCreate communityCreate){
        return new CommunityProjection(
            communityId,
            communityCreate.getName(),
            communityCreate.getAvatarId(),
            communityCreate.getSlug(),
            communityCreate.getIsPrivate(),
            toPermissions(communityCreate.getCommunityPermissionsMap()),
            true
        );
    }

    public CommunityProjection toEntity(Long communityId, CommunityUpdate communityUpdate){
        return new CommunityProjection(
            communityId,
            communityUpdate.getName(),
            communityUpdate.getAvatarId(),
            communityUpdate.getSlug(),
            communityUpdate.getIsPrivate(),
            toPermissions(communityUpdate.getCommunityPermissionsMap()),
            false
        );
    }


    private Set<CommunityPermission> toPermissions(Map<String, ModeratorPermissions> permissions){
        return permissions.entrySet()
            .stream()
            .flatMap(pair -> pair.getValue().getPermissionsList().stream()
                .map(permission -> new CommunityPermission(permission.name(), UUID.fromString(pair.getKey())))
            )
            .filter(t -> Permission.valueOf(t.getPermission()) == Permission.POST_DELETE)
            .collect(Collectors.toSet());
    }


}
