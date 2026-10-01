package com.ambrosia.content_service.post.utils.policy;

import java.util.UUID;

import org.springframework.util.Assert;

import com.ambrosia.content_service.community.model.dto.CommunityUserData;
import com.ambrosia.content_service.community.model.dto.PostUserData;
import com.ambrosia.content_service.exception.api.DoesntFollowedException;
import com.ambrosia.content_service.exception.api.NotEnoughPermissionsException;
import com.ambrosia.content_service.exception.api.UserBannedException;

public class UserActor extends AbstractPolicy implements PostPolicy {
    public UserActor(UUID userId){
        Assert.notNull(userId, "User id must not be null!");
        super(userId);
    }

    @Override
    public void validateCreate(CommunityUserData userData) {
        if(userData.isBanned())
            throw new UserBannedException();
        if(!userData.isFollowed())
            throw new DoesntFollowedException();
    }

    @Override
    public void validateReply(CommunityUserData userData) {
        if(userData.isBanned())
            throw new UserBannedException();
    }

    @Override
    public void validateView(CommunityUserData userData) {
        if((!userData.isFollowed() || userData.isBanned()) && userData.isCommunityPrivate())
            throw new UserBannedException();
    }

    @Override
    public void validateDelete(PostUserData userData) {
        if(userData.isModerator())
            return;
        if(!userData.authorId().equals(userId))
            throw new NotEnoughPermissionsException();
    }
}
