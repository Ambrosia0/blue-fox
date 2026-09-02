package com.ambrosia.community_service.community.utils.policy;

import com.ambrosia.community_service.community.model.entity.Community;

public sealed interface CommunityAccessPolicy permits AdminActor, UserActor{
    void validateOwnership(Community community);
    void validateOwnerEditing();
}
