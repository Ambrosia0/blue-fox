package com.ambrosia.community_service.community.domain.policy.manage;

import org.springframework.stereotype.Component;

import com.ambrosia.community_service.community.domain.policy.entity.CommunityCreationUserContext;
import com.ambrosia.community_service.core.AppConfiguration;
import com.ambrosia.community_service.exception.community.ExceededOwnedCommunityLimitException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class CommunityCreatePolicy implements GenericPolicy<CommunityCreationUserContext>{
    private final AppConfiguration appConfiguration;
    
    @Override
    public void evaluate(Actor actor, CommunityCreationUserContext policyData) {
        if(policyData.ownedCommunities() >= appConfiguration.getMaxOwnedCommunitiesPerUser())
            throw new ExceededOwnedCommunityLimitException();
    }
}
