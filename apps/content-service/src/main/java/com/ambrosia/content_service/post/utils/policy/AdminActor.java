package com.ambrosia.content_service.post.utils.policy;

import java.util.UUID;

public record AdminActor() implements PostOwnershipPolicy{
    @Override
    public void validatePostOwnership(UUID ownerId) {
    }
}
