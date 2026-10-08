package com.ambrosia.profile_service.user.infrastructure.keycloak.dto;

/**
 * @see "https://www.keycloak.org/docs-api/latest/rest-api/index.html#SocialLinkRepresentation"
 */
public record SocialLinkRepresentation(
    String socialProvider,
    String socialUserId,
    String socialUsername
) {}
