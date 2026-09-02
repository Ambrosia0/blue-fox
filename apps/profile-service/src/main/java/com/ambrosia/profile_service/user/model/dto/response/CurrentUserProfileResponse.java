package com.ambrosia.profile_service.user.model.dto.response;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import com.ambrosia.profile_service.user.utils.Status;

public record CurrentUserProfileResponse(
    UUID id,
    String firstName,
    String lastName,
    String username,
    String about,
    String email,
    UUID avatarId,
    Instant createdAt,
    Status status,
    Instant lastActivity,
    ProfileSettingsResponse settings
) implements Serializable{}
