package com.ambrosia.community_service.follow.application.policy;

import org.springframework.stereotype.Component;

import com.ambrosia.community_service.community.application.policy.CommunityUserDataRepository;
import com.ambrosia.community_service.core.domain.policy.entity.CommunityFollowUserContext;
import com.ambrosia.community_service.exception.community.CommunityDoesntExistException;
import com.ambrosia.community_service.follow.domain.policy.CommunityFollowPolicy;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.GenericPolicyHandler;
import com.ambrosia.library_policy.policy.registry.PolicyData;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class FollowPolicyHandler implements GenericPolicyHandler<CommunityFollowUserContext, Long>{
    private final CommunityFollowPolicy communityFollowPolicy;

    private final CommunityUserDataRepository communityUserDataRepository;

    @Override
    public PolicyData evaluate(Actor actor, Long arg) {
        var context = communityUserDataRepository.loadFollowUserData(actor.id(), arg)
            .orElseThrow(() -> new CommunityDoesntExistException());
        communityFollowPolicy.evaluate(actor, context);
        return context;
    }

    @Override
    public Class<? extends GenericPolicy<CommunityFollowUserContext>> policyType() {
        return CommunityFollowPolicy.class;
    }
}
