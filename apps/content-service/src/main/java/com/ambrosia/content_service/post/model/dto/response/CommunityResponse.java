package com.ambrosia.content_service.post.model.dto.response;

import java.util.UUID;

public record CommunityResponse(
    long id,
    String name,
    boolean isPrivate,
    UUID avatarId
) {}
