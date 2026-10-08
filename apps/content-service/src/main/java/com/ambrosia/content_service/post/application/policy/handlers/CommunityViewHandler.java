package com.ambrosia.content_service.post.application.policy.handlers;

import org.springframework.stereotype.Component;

import com.ambrosia.content_service.exception.api.CommunityDoesntExistException;
import com.ambrosia.content_service.post.application.policy.PostPolicyRepository;
import com.ambrosia.content_service.post.domain.policy.CommunityViewPolicy;
import com.ambrosia.content_service.post.domain.policy.entity.CommunityData;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.GenericPolicyHandler;
import com.ambrosia.library_policy.policy.registry.PolicyData;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class CommunityViewHandler implements GenericPolicyHandler<CommunityData, Long>{
    private final PostPolicyRepository policyRepository;

    private final CommunityViewPolicy policy;
    
    @Override
    public PolicyData evaluate(Actor actor, Long arg) {
        var data = policyRepository.loadCommunityData(actor.id(), arg)
            .orElseThrow(() -> new CommunityDoesntExistException());
        policy.evaluate(actor, data);
        return data;
    }

    @Override
    public Class<? extends GenericPolicy<CommunityData>> policyType() {
        return CommunityViewPolicy.class;
    }
}
