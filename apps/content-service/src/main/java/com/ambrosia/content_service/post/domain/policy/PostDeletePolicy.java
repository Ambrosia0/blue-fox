package com.ambrosia.content_service.post.domain.policy;

import org.springframework.stereotype.Component;

import com.ambrosia.content_service.exception.api.NotEnoughPermissionsException;
import com.ambrosia.content_service.post.domain.policy.entity.PostDeletePolicyData;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.Actor.Role;

@Component 
public class PostDeletePolicy implements GenericPolicy<PostDeletePolicyData>{
    @Override
    public void evaluate(Actor actor, PostDeletePolicyData policyData) {
        if(actor.role() == Role.ANONYMOUS)
            throw new NotEnoughPermissionsException();

        if(actor.id().equals(policyData.authorId()) || actor.role() == Role.ADMIN || policyData.hasDeletePermission())
            return;
        
        throw new NotEnoughPermissionsException();
    }
}
