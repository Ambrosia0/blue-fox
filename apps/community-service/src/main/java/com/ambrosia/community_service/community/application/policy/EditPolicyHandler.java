package com.ambrosia.community_service.community.application.policy;

import org.springframework.stereotype.Component;

import com.ambrosia.community_service.community.domain.policy.entity.CommunityEditUserContext;
import com.ambrosia.community_service.community.domain.policy.manage.CommunityEditPolicy;
import com.ambrosia.community_service.exception.community.CommunityDoesntExistException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.GenericPolicyHandler;
import com.ambrosia.library_policy.policy.registry.PolicyData;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class EditPolicyHandler implements GenericPolicyHandler<CommunityEditUserContext, CommunityEditArg>{
    private final CommunityEditPolicy communityEditPolicy;
    
    private final CommunityUserDataRepository communityUserDataRepository;
    
    @Override
    public PolicyData evaluate(Actor actor, CommunityEditArg arg) {
        var context = communityUserDataRepository.loadEditUserData(actor.id(), arg.communityId(), arg.userIds())
                .orElseThrow(() -> new CommunityDoesntExistException());
        communityEditPolicy.evaluate(actor, context);
        return context;
    }

    @Override
    public Class<? extends GenericPolicy<CommunityEditUserContext>> policyType() {
        return CommunityEditPolicy.class;
    }
}
