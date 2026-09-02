package com.ambrosia.community_service.community.service;

import java.util.UUID;

import com.ambrosia.community_service.community.utils.ScopeEnum;

public interface ScopeValidator {
    boolean hasScope(long communityId, ScopeEnum scope, UUID userId);
    boolean hasAnyScope(long communityId, UUID userId);
}
