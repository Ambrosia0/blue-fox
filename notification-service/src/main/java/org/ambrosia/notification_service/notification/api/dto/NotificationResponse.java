package org.ambrosia.notification_service.notification.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.Instant;

@RegisterForReflection
public record NotificationResponse(
    String content,

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    UserResponse user,

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    CommunityResponse community,

    Instant createdAt
) {}
