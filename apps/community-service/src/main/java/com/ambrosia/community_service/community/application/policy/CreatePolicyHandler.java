package com.ambrosia.community_service.community.application.policy;

import org.springframework.stereotype.Component;

import com.ambrosia.community_service.community.domain.policy.entity.CommunityCreationUserContext;
import com.ambrosia.community_service.community.domain.policy.manage.CommunityCreatePolicy;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicyHandler;
import com.ambrosia.library_policy.policy.registry.PolicyData;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class CreatePolicyHandler implements GenericPolicyHandler<CommunityCreationUserContext, Void>{
    private final CommunityCreatePolicy communityCreatePolicy;
    
    private final CommunityUserDataRepository communityUserDataRepository;

    @Override
    public PolicyData evaluate(Actor actor, Void arg) {
        var userContext = communityUserDataRepository.loadCreationUserData(actor.id());
        communityCreatePolicy.evaluate(actor, userContext);
        return userContext;
    }

    @Override
    public Class<CommunityCreatePolicy> policyType() {
        return CommunityCreatePolicy.class;
    }
}
