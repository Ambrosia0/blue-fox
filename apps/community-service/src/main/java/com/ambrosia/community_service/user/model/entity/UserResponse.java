package com.ambrosia.community_service.user.model.entity;

import java.io.Serializable;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String username,
    String firstName,
    String lastName,
    String avatarId
) implements Serializable{}
