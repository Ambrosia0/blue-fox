package com.ambrosia.content_service.post.utils.policy;

import java.util.UUID;

import com.ambrosia.content_service.community.model.dto.CommunityUserData;
import com.ambrosia.content_service.exception.api.DoesntFollowedException;
import com.ambrosia.content_service.exception.api.NotEnoughPermissionsException;
import com.ambrosia.content_service.exception.api.UserBannedException;

public record UserActor(
    UUID userId
) implements PostPolicy {
    @Override
    public void validatePostOwnership(UUID ownerId) {
        if(!userId.equals(ownerId))
            throw new NotEnoughPermissionsException();
    }

    @Override
    public void validateCreate(CommunityUserData userData) {
        if(userData.isBanned())
            throw new UserBannedException();
        if(!userData.isFollowed())
            throw new DoesntFollowedException();
    }
}
