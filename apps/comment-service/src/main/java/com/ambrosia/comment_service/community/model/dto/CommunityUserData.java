package com.ambrosia.comment_service.community.model.dto;

public record CommunityUserData(
    boolean isCommunityPrivate,
    boolean isFollowed,
    boolean isBanned
) {}
