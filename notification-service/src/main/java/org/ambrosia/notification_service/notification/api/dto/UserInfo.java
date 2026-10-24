package org.ambrosia.notification_service.notification.api.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record UserInfo(
    String id,
    String username,
    String avatarId
) {}
