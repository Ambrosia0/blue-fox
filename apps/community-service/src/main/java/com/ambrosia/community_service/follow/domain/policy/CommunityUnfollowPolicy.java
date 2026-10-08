package com.ambrosia.community_service.follow.domain.policy;

import org.springframework.stereotype.Component;

import com.ambrosia.community_service.core.domain.policy.entity.CommunityUserContext;
import com.ambrosia.community_service.exception.follow.DoesntFollowedException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;

@Component 
public class CommunityUnfollowPolicy implements GenericPolicy<CommunityUserContext>{

    @Override
    public void evaluate(Actor actor, CommunityUserContext policyData) {
        if(!policyData.isFollowed())
            throw new DoesntFollowedException();        
    }
}
