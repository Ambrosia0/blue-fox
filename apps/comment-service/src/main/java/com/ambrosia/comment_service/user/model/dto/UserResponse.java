package com.ambrosia.comment_service.user.model.dto;

import java.util.UUID;

public record UserResponse(
    UUID id,
    String username,
    String firstName,
    String lastName,
    String avatarId
) {}
