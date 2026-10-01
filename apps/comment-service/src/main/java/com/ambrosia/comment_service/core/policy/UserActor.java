package com.ambrosia.comment_service.core.policy;

import java.util.UUID;

import org.springframework.util.Assert;

import com.ambrosia.comment_service.community.model.dto.CommentUserData;
import com.ambrosia.comment_service.community.model.dto.CommunityUserData;
import com.ambrosia.comment_service.community.utils.CommentPolicy;
import com.ambrosia.comment_service.exceptions.api.DoesntFollowedOnPrivateCommunityException;
import com.ambrosia.comment_service.exceptions.api.NotEnoughPermissionsException;
import com.ambrosia.comment_service.exceptions.api.UserBannedException;

public class UserActor extends AbstractPolicy implements CommentPolicy{
    public UserActor(UUID userId){
        Assert.notNull(userId, "User id must not be null!");
        super(userId);    
    }

    @Override
    public void validateCommentView(CommunityUserData communityUserData) {
        if(communityUserData == null)
            return;
        if(communityUserData.isCommunityPrivate() && 
            (!communityUserData.isFollowed() || communityUserData.isBanned()))
            throw new DoesntFollowedOnPrivateCommunityException();
    }

    @Override
    public void validateCommentCreate(CommunityUserData communityUserData) {
        if(communityUserData == null)
            return;
        if(communityUserData.isBanned())
            throw new UserBannedException();
        if(communityUserData.isCommunityPrivate() && !communityUserData.isFollowed()) 
            throw new DoesntFollowedOnPrivateCommunityException();
    }

    @Override
    public void validateCommentLike(CommunityUserData communityUserData) {
        validateCommentCreate(communityUserData);
    }

    @Override
    public void validateDelete(CommentUserData commentUserData) {
        if(commentUserData.isModerator())
            return;
        if(!commentUserData.userId().equals(userId))
            throw new NotEnoughPermissionsException();
    }
}
