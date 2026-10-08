package com.ambrosia.community_service.community.domain.policy.manage;

import org.springframework.stereotype.Component;

import com.ambrosia.community_service.community.domain.policy.entity.CommunityEditUserContext;
import com.ambrosia.community_service.exception.community.NotEnoughPermissionsException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.Actor.Role;

@Component 
public class CommunityDeletePolicy implements GenericPolicy<CommunityEditUserContext>{
    @Override
    public void evaluate(Actor actor, CommunityEditUserContext policyData) {
        if(actor.id().equals(policyData.ownerId()) || actor.role() == Role.ADMIN)
            return;
        throw new NotEnoughPermissionsException();
    }
}
