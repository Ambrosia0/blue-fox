package com.ambrosia.community_service.community.domain.policy.manage;

import org.springframework.stereotype.Component;

import com.ambrosia.community_service.exception.community.NotEnoughPermissionsException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.EmptyPolicyData;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.Actor.Role;

@Component 
public class CommunityOwnerEditPolicy implements GenericPolicy<EmptyPolicyData>{
    @Override
    public void evaluate(Actor actor, EmptyPolicyData policyData) {
        if(actor.role() == Role.ADMIN)
            return;
        throw new NotEnoughPermissionsException();
    }
}
