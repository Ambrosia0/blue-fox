package com.ambrosia.content_service.post.utils.policy;

import java.util.UUID;

import com.ambrosia.content_service.community.model.dto.CommunityUserData;

public interface PostPolicy {
    void validateCreate(CommunityUserData userData);
    void validatePostOwnership(UUID ownerId);
    void validateReply(CommunityUserData userData);
}
