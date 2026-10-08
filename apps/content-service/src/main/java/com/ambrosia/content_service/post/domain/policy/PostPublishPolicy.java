package com.ambrosia.content_service.post.domain.policy;

import org.springframework.stereotype.Component;

import com.ambrosia.content_service.exception.api.DoesntFollowedException;
import com.ambrosia.content_service.exception.api.NotEnoughPermissionsException;
import com.ambrosia.content_service.exception.api.PrivateReplyException;
import com.ambrosia.content_service.exception.api.UserBannedException;
import com.ambrosia.content_service.post.domain.policy.entity.CreateCommunityData;
import com.ambrosia.content_service.post.domain.policy.entity.PostPublishPolicyData;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.Actor.Role;

@Component 
public class PostPublishPolicy implements GenericPolicy<PostPublishPolicyData>{
    @Override
    public void evaluate(Actor actor, PostPublishPolicyData policyData) {
        if(actor.role() == Role.ANONYMOUS || !actor.id().equals(policyData.authorId()))
            throw new NotEnoughPermissionsException();
        
        policyData.postedCommunity().ifPresent(posted -> {
            if(policyData.replyingPostCommunity().isEmpty()){
                validatePostInCommunity(actor, posted);
                return;
            }
            var replying = policyData.replyingPostCommunity().get();

            if((posted.isPrivate() || replying.isPrivate()) && !posted.communityId().equals(replying.communityId()))
                throw new PrivateReplyException();
            validatePostInCommunity(actor, posted);
            validateReplyToPost(actor, posted);
        });
    }

    private void validatePostInCommunity(Actor actor, CreateCommunityData data){
        if(data.isModerator() || actor.role() == Role.ADMIN)
            return;
        if(data.isBanned())
            throw new UserBannedException();
        if(!data.isFollowed())
            throw new DoesntFollowedException();
    }

    private void validateReplyToPost(Actor actor, CreateCommunityData data){
        if(data.isModerator() || actor.role() == Role.ADMIN)
            return;
        if(data.isBanned())
            throw new UserBannedException();
    }
}
