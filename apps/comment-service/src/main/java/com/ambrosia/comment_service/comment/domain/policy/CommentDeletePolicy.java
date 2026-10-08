package com.ambrosia.comment_service.comment.domain.policy;

import org.springframework.stereotype.Component;

import com.ambrosia.comment_service.comment.domain.policy.entity.DeletePolicyData;
import com.ambrosia.comment_service.exceptions.api.NotEnoughPermissionsException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.Actor.Role;

@Component 
public class CommentDeletePolicy implements GenericPolicy<DeletePolicyData>{
    @Override
    public void evaluate(Actor actor, DeletePolicyData data) {
        if(actor.role() == Role.ANONYMOUS)
            throw new NotEnoughPermissionsException();
        if(actor.id().equals(data.authorId()) || actor.role() == Role.ADMIN || data.isModerator())
            return;
        throw new NotEnoughPermissionsException();
    }
}
