package com.ambrosia.community_service.community.application.impl;

import org.springframework.stereotype.Service;

import com.ambrosia.community_service.community.application.query.CommunityQueryService;
import com.ambrosia.community_service.community.application.query.model.CommunityResponse;
import com.ambrosia.community_service.community.domain.policy.view.CommunityViewPolicy;
import com.ambrosia.community_service.community.infrastructure.cache.CommunitySlugCache;
import com.ambrosia.community_service.community.infrastructure.cache.CommunityUserDataCache;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicyContext;
import com.ambrosia.library_policy.policy.Actor.Role;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserCommunityServiceImpl implements CommunityQueryService{
    private final CommunitySlugCache communitySlugCache;

    private final CommunityUserDataCache communityUserDataCache;

    private final GenericPolicyContext genericPolicyContext;

    @Override
    public CommunityResponse getCommunity(String slug, Actor actor) {
        genericPolicyContext.evaluate(actor, CommunityViewPolicy.class, slug);

        var communityResp = communitySlugCache.findCommunity(slug);

        if(actor.role() == Role.ANONYMOUS)
            return communityResp;

        communityResp.setCommunityUserData(
            communityUserDataCache.findUserData(actor.id(), communityResp.getId())
        );
        return communityResp;
    }

}
