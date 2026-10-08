package com.ambrosia.community_service.community.domain.policy.manage;

import org.springframework.stereotype.Component;

import com.ambrosia.community_service.community.domain.policy.entity.CommunityEditUserContext;
import com.ambrosia.community_service.exception.community.NotEnoughPermissionsException;
import com.ambrosia.community_service.exception.community.UserDoesntExistException;
import com.ambrosia.community_service.exception.community.UserIsBannedException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.Actor.Role;

@Component 
public class CommunityEditPolicy implements GenericPolicy<CommunityEditUserContext>{
    @Override
    public void evaluate(Actor actor, CommunityEditUserContext policyData) {
        if(actor.role() == Role.ADMIN || actor.id().equals(policyData.ownerId())){
            if(policyData.isAnyTargetBanned())
                throw new UserIsBannedException();
            if(!policyData.isAllTargetsExists())
                throw new UserDoesntExistException();
            return;
        }else{
            throw new NotEnoughPermissionsException();
        }
    }
}
