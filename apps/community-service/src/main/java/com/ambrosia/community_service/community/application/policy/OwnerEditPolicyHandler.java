package com.ambrosia.community_service.community.application.policy;

import org.springframework.stereotype.Component;

import com.ambrosia.community_service.community.domain.policy.manage.CommunityOwnerEditPolicy;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.EmptyPolicyData;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.GenericPolicyHandler;
import com.ambrosia.library_policy.policy.registry.PolicyData;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class OwnerEditPolicyHandler implements GenericPolicyHandler<EmptyPolicyData, Void>{
    private final CommunityOwnerEditPolicy communityOwnerEditPolicy;
    
    @Override
    public PolicyData evaluate(Actor actor, Void arg) {
        communityOwnerEditPolicy.evaluate(actor, null);
        return null;
    }

    @Override
    public Class<? extends GenericPolicy<EmptyPolicyData>> policyType() {
        return CommunityOwnerEditPolicy.class;
    }
}
