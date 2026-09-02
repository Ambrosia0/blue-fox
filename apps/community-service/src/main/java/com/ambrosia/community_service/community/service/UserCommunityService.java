package com.ambrosia.community_service.community.service;

import java.util.UUID;

import com.ambrosia.community_service.community.model.dto.response.CommunityResponse;

import jakarta.annotation.Nullable;

public interface UserCommunityService {
    CommunityResponse getCommunity(String slug, @Nullable UUID requestingUser);
}
