package com.ambrosia.community_service.community.domain.policy.view;

import org.springframework.stereotype.Component;

import com.ambrosia.community_service.core.domain.policy.entity.CommunityUserContext;
import com.ambrosia.community_service.exception.community.NotEnoughPermissionsException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.Actor.Role;

@Component 
public class CommunityViewPolicy implements GenericPolicy<CommunityUserContext>{
    public void evaluate(Actor actor, CommunityUserContext communityUserData){
        if(communityUserData.isPrivate() && actor.role() == Role.ANONYMOUS)
            throw new NotEnoughPermissionsException();
        
        if(actor.role() == Role.ADMIN || communityUserData.isModerator())
            return;

        if((communityUserData.isBanned() || !communityUserData.isFollowed()) && 
                communityUserData.isPrivate())
            throw new NotEnoughPermissionsException();
    }
}
