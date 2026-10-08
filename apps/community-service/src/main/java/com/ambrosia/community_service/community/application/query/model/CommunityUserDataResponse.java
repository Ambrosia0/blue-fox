package com.ambrosia.community_service.community.application.query.model;

import java.io.Serializable;

import com.ambrosia.community_service.community.utils.ScopeEnum;

public record CommunityUserDataResponse(
    boolean isFollowed,
    ScopeEnum[] scopes
) implements Serializable{}
