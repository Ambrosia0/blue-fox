package com.ambrosia.content_service.community.model.dto;

public record CommunityUserData(
    boolean isCommunityPrivate,
    boolean isFollowed,
    boolean isBanned
) {}
