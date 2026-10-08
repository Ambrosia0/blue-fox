package com.ambrosia.content_service.post.application.policy.handlers;

import org.springframework.stereotype.Component;

import com.ambrosia.content_service.post.application.policy.PostPolicyRepository;
import com.ambrosia.content_service.post.domain.policy.PostViewPolicy;
import com.ambrosia.content_service.post.domain.policy.entity.PostViewPolicyData;
import com.ambrosia.content_service.post.exception.PostDoesntExistException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.GenericPolicyHandler;
import com.ambrosia.library_policy.policy.registry.PolicyData;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class PostViewPolicyHandler implements GenericPolicyHandler<PostViewPolicyData, Long>{
    private final PostViewPolicy policy;

    private final PostPolicyRepository policyRepository;

    @Override
    public PolicyData evaluate(Actor actor, Long arg) {
        var data = policyRepository.loadForView(actor.id(), arg)
            .orElseThrow(() -> new PostDoesntExistException());
        policy.evaluate(actor, data);
        return data;
    }

    @Override
    public Class<? extends GenericPolicy<PostViewPolicyData>> policyType() {
        return PostViewPolicy.class;
    }
}
