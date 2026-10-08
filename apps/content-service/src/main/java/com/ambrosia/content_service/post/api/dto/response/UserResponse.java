package com.ambrosia.content_service.post.api.dto.response;

import java.io.Serializable;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String username,
    String firstName,
    String lastName,
    String avatarId
) implements Serializable{}
