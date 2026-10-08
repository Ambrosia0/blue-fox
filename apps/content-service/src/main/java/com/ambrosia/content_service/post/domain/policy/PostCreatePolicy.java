package com.ambrosia.content_service.post.domain.policy;

import org.springframework.stereotype.Component;

import com.ambrosia.content_service.exception.api.DoesntFollowedException;
import com.ambrosia.content_service.exception.api.PrivateReplyException;
import com.ambrosia.content_service.exception.api.UserBannedException;
import com.ambrosia.content_service.post.domain.policy.entity.CreateCommunityData;
import com.ambrosia.content_service.post.domain.policy.entity.PostCreatePolicyData;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.Actor.Role;

@Component 
public class PostCreatePolicy implements GenericPolicy<PostCreatePolicyData>{
    @Override
    public void evaluate(Actor actor, PostCreatePolicyData data) {
        if(data.postedCommunity().isEmpty() && data.replyingPost().isEmpty())
            return;

        data.postedCommunity().ifPresentOrElse(
            posted ->{
                if(data.replyingPost().isPresent() && data.replyingPost().get().communityData().isPresent()){
                    var replying = data.replyingPost().get().communityData().get();
                    if((posted.isPrivate() || replying.isPrivate()) && !posted.communityId().equals(replying.communityId()))
                        throw new PrivateReplyException();
                    validatePostInCommunity(actor, posted);
                    validateReplyToPost(actor, replying);
                }else{
                    validatePostInCommunity(actor, posted);
                }
            },
            () -> {
                if(data.replyingPost().isPresent() && data.replyingPost().get().communityData().isPresent())
                    validateReplyToPost(actor, data.replyingPost().get().communityData().get());
            }
        );
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
