package com.ambrosia.content_service.post.application.policy.handlers;

import org.springframework.stereotype.Component;

import com.ambrosia.content_service.exception.api.CommunityDoesntExistException;
import com.ambrosia.content_service.post.application.policy.PostPolicyRepository;
import com.ambrosia.content_service.post.domain.policy.PostCreatePolicy;
import com.ambrosia.content_service.post.domain.policy.entity.PostCreatePolicyData;
import com.ambrosia.content_service.post.exception.PostDoesntExistException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.GenericPolicyHandler;
import com.ambrosia.library_policy.policy.registry.PolicyData;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class CreatePostPolicyHandler implements GenericPolicyHandler<PostCreatePolicyData, CreateHandlerArg>{
    private final PostPolicyRepository policyRepository;
    
    private final PostCreatePolicy policy;
    
    @Override
    public PolicyData evaluate(Actor actor, CreateHandlerArg arg) {
        var data = policyRepository.loadForCreate(actor.id(), arg.replyingPostId(), arg.communityId());
        if(arg.communityId() != null && data.postedCommunity().isEmpty())
            throw new CommunityDoesntExistException();
        if(arg.replyingPostId() != null && data.replyingPost().isEmpty())
            throw new PostDoesntExistException();

        policy.evaluate(actor, data);
        return data;
    }

    @Override
    public Class<? extends GenericPolicy<PostCreatePolicyData>> policyType() {
        return PostCreatePolicy.class;
    }
}
