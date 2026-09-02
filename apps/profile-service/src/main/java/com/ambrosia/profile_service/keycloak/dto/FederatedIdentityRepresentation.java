package com.ambrosia.profile_service.keycloak.dto;

/**
 * @see "https://www.keycloak.org/docs-api/latest/rest-api/index.html#FederatedIdentityRepresentation"
 */
public record FederatedIdentityRepresentation(
    String identityProvider,
    String userId,
    String userName
) {}
