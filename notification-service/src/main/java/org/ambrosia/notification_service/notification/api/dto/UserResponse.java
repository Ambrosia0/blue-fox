package org.ambrosia.notification_service.notification.api.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.Instant;
import java.util.UUID;

@RegisterForReflection
public record UserResponse(
    UUID id,
    String username,
    String avatarId
) {}
