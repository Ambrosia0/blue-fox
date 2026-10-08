package com.ambrosia.content_service.post.domain.policy;

import org.springframework.stereotype.Component;

import com.ambrosia.content_service.exception.api.DoesntFollowedException;
import com.ambrosia.content_service.exception.api.NotEnoughPermissionsException;
import com.ambrosia.content_service.exception.api.UserBannedException;
import com.ambrosia.content_service.post.domain.policy.entity.CommunityData;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.Actor.Role;

@Component 
public class CommunityViewPolicy implements GenericPolicy<CommunityData>{
    @Override
    public void evaluate(Actor actor, CommunityData data) {
        if(data.isModerator() || actor.role() == Role.ADMIN)
            return;
        if(data.isPrivate()){
            if(actor.role() == Role.ANONYMOUS)
                throw new NotEnoughPermissionsException();
            if(!data.isFollowed())
                throw new DoesntFollowedException();
            if(data.isBanned())
                throw new UserBannedException();
        }
    }
}
