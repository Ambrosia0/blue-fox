package com.ambrosia.comment_service.core.policy;

import com.ambrosia.comment_service.community.model.dto.CommentUserData;
import com.ambrosia.comment_service.community.model.dto.CommunityUserData;
import com.ambrosia.comment_service.community.utils.CommentPolicy;
import com.ambrosia.comment_service.exceptions.api.DoesntFollowedOnPrivateCommunityException;
import com.ambrosia.comment_service.exceptions.api.NotEnoughPermissionsException;

public class AnonymousActor extends AbstractPolicy implements CommentPolicy{
    public AnonymousActor(){
        super(null);
    }

    @Override
    public void validateCommentCreate(CommunityUserData communityUserData) {
        throw new NotEnoughPermissionsException();
    }

    @Override
    public void validateCommentLike(CommunityUserData communityUserData) {
        throw new NotEnoughPermissionsException();
    }

    @Override
    public void validateCommentView(CommunityUserData communityUserData) {
        if(communityUserData.isCommunityPrivate())
            throw new DoesntFollowedOnPrivateCommunityException();
    }

    @Override
    public void validateDelete(CommentUserData commentUserData) {
        throw new NotEnoughPermissionsException();
    }
}
