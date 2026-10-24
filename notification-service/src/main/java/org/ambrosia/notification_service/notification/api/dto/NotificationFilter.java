package org.ambrosia.notification_service.notification.api.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.ws.rs.QueryParam;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@RegisterForReflection
public record NotificationFilter(
    @QueryParam(value = "lastSeenTime")
    Instant lastSeenInstant,

    @QueryParam(value = "lastSeenId")
    UUID lastSeenId
) {}
