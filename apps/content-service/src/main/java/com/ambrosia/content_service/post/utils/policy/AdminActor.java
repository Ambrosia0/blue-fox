package com.ambrosia.content_service.post.utils.policy;

import java.util.UUID;

import com.ambrosia.content_service.community.model.dto.CommunityUserData;

public record AdminActor() implements PostPolicy{
    @Override
    public void validatePostOwnership(UUID ownerId) {}

    @Override
    public void validateCreate(CommunityUserData userData) {}
}
