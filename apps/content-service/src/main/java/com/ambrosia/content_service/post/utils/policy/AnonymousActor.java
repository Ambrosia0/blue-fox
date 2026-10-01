package com.ambrosia.content_service.post.utils.policy;

import com.ambrosia.content_service.community.model.dto.CommunityUserData;
import com.ambrosia.content_service.community.model.dto.PostUserData;
import com.ambrosia.content_service.exception.api.NotEnoughPermissionsException;

public class AnonymousActor extends AbstractPolicy implements PostPolicy {
    public AnonymousActor(){
        super(null);
    }

    @Override
    public void validateCreate(CommunityUserData userData) {
        throw new NotEnoughPermissionsException();
    }

    @Override
    public void validateReply(CommunityUserData userData) {
        throw new NotEnoughPermissionsException();
    }

    @Override
    public void validateView(CommunityUserData userData) {
        if(userData.isCommunityPrivate())
            throw new NotEnoughPermissionsException();
    }

    @Override
    public void validateDelete(PostUserData userData) {
        throw new NotEnoughPermissionsException();
    }
}
