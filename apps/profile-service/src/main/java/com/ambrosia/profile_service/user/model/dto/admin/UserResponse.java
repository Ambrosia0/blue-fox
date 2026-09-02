package com.ambrosia.profile_service.user.model.dto.admin;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.ambrosia.profile_service.user.utils.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public record UserResponse(
    UUID id,
    String username,
    String firstName,
    String lastName,
    boolean isEnabled,
    String email,
    UUID avatarId,
    Long followCount,
    Instant createdAt,
    Status status,
    Instant lastActivity,

    @JsonInclude(value = Include.NON_NULL)
    List<UsernameChange> usernameHistory
) {}
