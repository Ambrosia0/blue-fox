package com.ambrosia.content_service.post.utils.policy;

import java.util.UUID;

public interface PostOwnershipPolicy {
    void validatePostOwnership(UUID ownerId);
}
