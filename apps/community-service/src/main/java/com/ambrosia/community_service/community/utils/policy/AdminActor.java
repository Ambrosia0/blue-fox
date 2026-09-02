package com.ambrosia.community_service.community.utils.policy;

import com.ambrosia.community_service.community.model.entity.Community;

public record AdminActor() implements CommunityAccessPolicy{
    @Override
    public void validateOwnership(Community community) {
        return;
    }

    @Override
    public void validateOwnerEditing() {
        return;
    }
}
