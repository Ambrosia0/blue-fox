package com.ambrosia.community_service.follow.domain.policy;

import org.springframework.stereotype.Component;

import com.ambrosia.community_service.core.domain.policy.entity.CommunityFollowUserContext;
import com.ambrosia.community_service.exception.follow.AlreadyFollowedException;
import com.ambrosia.community_service.exception.follow.AlreadyRequestedFollowException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;

@Component 
public class CommunityFollowPolicy implements GenericPolicy<CommunityFollowUserContext>{
    @Override
    public void evaluate(Actor actor, CommunityFollowUserContext policyData) {
        if(policyData.isFollowed())
            throw new AlreadyFollowedException();
        if(policyData.isRequested())
            throw new AlreadyRequestedFollowException();
    }
}
