package com.ambrosia.community_service.community.application.policy;

import org.springframework.stereotype.Component;

import com.ambrosia.community_service.community.domain.policy.entity.CommunityEditUserContext;
import com.ambrosia.community_service.community.domain.policy.manage.CommunityDeletePolicy;
import com.ambrosia.community_service.exception.community.CommunityDoesntExistException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.GenericPolicyHandler;
import com.ambrosia.library_policy.policy.registry.PolicyData;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class DeletePolicyHandler implements GenericPolicyHandler<CommunityEditUserContext, CommunityEditArg>{
    private final CommunityDeletePolicy communityDeletePolicy;
    
    private final CommunityUserDataRepository communityUserDataRepository;
    
    @Override
    public PolicyData evaluate(Actor actor, CommunityEditArg arg) {
        var context = communityUserDataRepository.loadEditUserData(
            actor.id(), 
            arg.communityId(), 
            null
        ).orElseThrow(() -> new CommunityDoesntExistException());
        communityDeletePolicy.evaluate(actor, context);
        return context;
    }

    @Override
    public Class<? extends GenericPolicy<CommunityEditUserContext>> policyType() {
        return CommunityDeletePolicy.class;
    }
}
