package com.ambrosia.community_service.community.application.policy;

import java.util.Set;
import java.util.UUID;

public record CommunityEditArg(
    Long communityId, 
    Set<UUID> userIds
) {
    public static CommunityEditArg create(Long communityId, Set<UUID> userIds){
        return new CommunityEditArg(communityId, userIds);
    }
}
