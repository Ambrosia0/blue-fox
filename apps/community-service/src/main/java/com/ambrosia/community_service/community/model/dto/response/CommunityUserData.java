package com.ambrosia.community_service.community.model.dto.response;

import java.io.Serializable;

import com.ambrosia.community_service.community.utils.ScopeEnum;

public record CommunityUserData(
    boolean isFollowed,
    ScopeEnum[] scopes
) implements Serializable {}
