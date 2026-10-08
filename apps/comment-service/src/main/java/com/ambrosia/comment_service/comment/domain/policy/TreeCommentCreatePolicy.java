package com.ambrosia.comment_service.comment.domain.policy;

import org.springframework.stereotype.Component;

import com.ambrosia.comment_service.comment.domain.policy.entity.CommentPolicyData;
import com.ambrosia.comment_service.comment.domain.policy.entity.CommunityData;
import com.ambrosia.comment_service.exceptions.api.UserBannedException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.Actor.Role;

@Component 
public class TreeCommentCreatePolicy implements GenericPolicy<CommentPolicyData>{
    @Override
    public void evaluate(Actor actor, CommentPolicyData data) {
        if(data.community().isEmpty())
            return;

        CommunityData community = data.community().get();
        if(actor.role() == Role.ADMIN || community.isModerator())
            return;

        if(community.isBanned())
            throw new UserBannedException();
    }
}
