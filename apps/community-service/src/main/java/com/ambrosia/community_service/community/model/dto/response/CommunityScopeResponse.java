package com.ambrosia.community_service.community.model.dto.response;

import java.util.UUID;

import com.ambrosia.community_service.community.utils.ScopeEnum;

public record CommunityScopeResponse(
    UUID userId,
    ScopeEnum[] scopes
) {}
