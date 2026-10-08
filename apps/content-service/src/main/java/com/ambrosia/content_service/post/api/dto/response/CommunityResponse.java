package com.ambrosia.content_service.post.api.dto.response;

import java.io.Serializable;

public record CommunityResponse(
    long id,
    String name,
    String slug,
    boolean isPrivate,
    String avatarId
) implements Serializable{}
