package com.ambrosia.community_service.core.application.policy;

import java.util.UUID;

import jakarta.annotation.Nullable;

public record ModeratorPolicyArg(
    Long communityId,
    UUID targetUserId
) {
    public static ModeratorPolicyArg create(Long communityId, @Nullable UUID targetUserId){
        return new ModeratorPolicyArg(communityId, targetUserId);
    }
}
