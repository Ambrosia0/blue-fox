package com.ambrosia.content_service.post.utils.policy;

import java.util.UUID;

import com.ambrosia.content_service.exception.api.NotEnoughPermissionsException;

public record UserActor(
    UUID userId
) implements PostOwnershipPolicy {
    @Override
    public void validatePostOwnership(UUID ownerId) {
        if(!userId.equals(ownerId))
            throw new NotEnoughPermissionsException();
    }
}
