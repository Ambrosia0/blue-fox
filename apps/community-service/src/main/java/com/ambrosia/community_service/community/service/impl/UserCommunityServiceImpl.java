package com.ambrosia.community_service.community.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.community_service.community.model.dto.response.CommunityResponse;
import com.ambrosia.community_service.community.service.UserCommunityService;
import com.ambrosia.community_service.community.service.cache.CommunitySlugCache;
import com.ambrosia.community_service.community.service.cache.CommunityUserDataCache;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserCommunityServiceImpl implements UserCommunityService{
    private final CommunitySlugCache communitySlugCache;

    private final CommunityUserDataCache communityUserDataCache;

    @Override
    public CommunityResponse getCommunity(String slug, @Nullable UUID requestingUser) {
        var communityResp =  communitySlugCache.findCommunity(slug);
        if(requestingUser == null)
            return communityResp;

        communityResp.setCommunityUserData(
            communityUserDataCache.findUserData(requestingUser, communityResp.getId())
        );
        return communityResp;
    }

}
