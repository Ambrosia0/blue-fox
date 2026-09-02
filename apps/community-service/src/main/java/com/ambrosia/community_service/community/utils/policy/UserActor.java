package com.ambrosia.community_service.community.utils.policy;

import java.util.UUID;

import com.ambrosia.community_service.community.model.entity.Community;
import com.ambrosia.community_service.exception.community.NotEnoughPermissionsException;

public record UserActor(
    UUID userId
) implements CommunityAccessPolicy {
    @Override
    public void validateOwnership(Community community) {
        if(community.getOwnerId() == null || !community.getOwnerId().equals(userId))
            throw new NotEnoughPermissionsException();
    }

    @Override
    public void validateOwnerEditing() {
        throw new NotEnoughPermissionsException();
    }
}
