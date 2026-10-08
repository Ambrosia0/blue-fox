package com.ambrosia.community_service.core.application.policy;

import org.springframework.stereotype.Component;

import com.ambrosia.community_service.community.application.policy.CommunityUserDataRepository;
import com.ambrosia.community_service.core.domain.policy.entity.CommunityModeratorUserContext;
import com.ambrosia.community_service.core.domain.policy.moderation.CommunityBanPolicy;
import com.ambrosia.community_service.exception.community.CommunityDoesntExistException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicy;
import com.ambrosia.library_policy.policy.GenericPolicyHandler;
import com.ambrosia.library_policy.policy.registry.PolicyData;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class BanPolicyHandler implements 
        GenericPolicyHandler<CommunityModeratorUserContext, ModeratorPolicyArg>{

    private final CommunityUserDataRepository communityUserDataRepository;

    private final CommunityBanPolicy communityBanPolicy;

    @Override
    public PolicyData evaluate(Actor actor, ModeratorPolicyArg arg) {
        var context = communityUserDataRepository.loadModeratorUserData(
                actor.id(),  
                arg.communityId(),
                arg.targetUserId()
            )
            .orElseThrow(() -> new CommunityDoesntExistException());
        communityBanPolicy.evaluate(actor, context);
        return context;
    }

    @Override
    public Class<? extends GenericPolicy<CommunityModeratorUserContext>> policyType() {
        return CommunityBanPolicy.class;
    }
}
