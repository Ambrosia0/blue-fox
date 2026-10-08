package com.ambrosia.community_service.core.domain.policy.moderation;

import org.springframework.stereotype.Component;

import com.ambrosia.community_service.community.utils.ScopeEnum;
import com.ambrosia.community_service.core.domain.policy.entity.CommunityModeratorUserContext;
import com.ambrosia.community_service.exception.community.NotEnoughPermissionsException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;

@Component 
public class CommunityFollowManagePolicy implements GenericPolicy<CommunityModeratorUserContext>{
    @Override
    public void evaluate(Actor actor, CommunityModeratorUserContext policyData) {
        if(!policyData.scopes().contains(ScopeEnum.FOLLOW_MANAGE)){
            throw new NotEnoughPermissionsException();
        }
    }
}
