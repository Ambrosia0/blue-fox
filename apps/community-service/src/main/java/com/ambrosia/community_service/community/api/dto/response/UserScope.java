package com.ambrosia.community_service.community.api.dto.response;

import java.util.UUID;

import com.ambrosia.community_service.community.utils.ScopeEnum;

public record UserScope(
    UUID userId,
    ScopeEnum scopeType,
    long communityId
) {}
