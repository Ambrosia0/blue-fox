package com.ambrosia.community_service.community.application.policy;

import org.springframework.stereotype.Component;

import com.ambrosia.community_service.community.domain.policy.view.CommunityViewPolicy;
import com.ambrosia.community_service.core.domain.policy.entity.CommunityUserContext;
import com.ambrosia.community_service.exception.community.CommunityDoesntExistException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.GenericPolicyHandler;
import com.ambrosia.library_policy.policy.registry.PolicyData;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class ViewPolicyHandler implements GenericPolicyHandler<CommunityUserContext, Object>{
    private final CommunityViewPolicy communityViewPolicy;
    
    private final CommunityUserDataRepository communityUserDataRepository;

    @Override
    public PolicyData evaluate(Actor actor, Object arg) {
        var context = (
                switch (arg) {
                    case String slug -> communityUserDataRepository.loadCommunityUserData(actor.id(), slug);
                    case Long id -> communityUserDataRepository.loadCommunityUserData(actor.id(), id);
                    default -> throw new IllegalArgumentException("Invalid argument!");
                }
            ).orElseThrow(() -> new CommunityDoesntExistException());

        communityViewPolicy.evaluate(actor, context);
        return context;
    }

    @Override
    public Class<? extends GenericPolicy<CommunityUserContext>> policyType() {
        return CommunityViewPolicy.class;
    }
}
