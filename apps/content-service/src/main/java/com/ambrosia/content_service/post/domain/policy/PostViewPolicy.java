package com.ambrosia.content_service.post.domain.policy;

import org.springframework.stereotype.Component;

import com.ambrosia.content_service.exception.api.DoesntFollowedException;
import com.ambrosia.content_service.exception.api.NotEnoughPermissionsException;
import com.ambrosia.content_service.exception.api.UserBannedException;
import com.ambrosia.content_service.post.domain.policy.entity.PostViewPolicyData;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.Actor.Role;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class PostViewPolicy implements GenericPolicy<PostViewPolicyData>{
    @Override
    public void evaluate(Actor actor, PostViewPolicyData policyData) {
        if(actor.role() == Role.ADMIN)
            return;

        if(policyData.communityData().isPrivate()){
            if(policyData.communityData().isModerator())
                return;
            if(actor.role() == Role.ANONYMOUS)
                throw new NotEnoughPermissionsException();
            if(!policyData.communityData().isFollowed())
                throw new DoesntFollowedException();
            if(policyData.communityData().isBanned())
                throw new UserBannedException();
        }
    }
}
