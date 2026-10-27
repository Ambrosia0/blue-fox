package org.ambrosia.notification_service.notification.api.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record CommunityResponse(
    Long id,
    String slug,
    String displayedName,
    String avatarId
){}
