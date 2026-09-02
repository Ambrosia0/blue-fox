package com.ambrosia.profile_service.keycloak.dto;

import java.util.List;

/**
 * @see "https://www.keycloak.org/docs-api/latest/rest-api/index.html#UserConsentRepresentation"
 */
public record UserConsentRepresentation(
    String clientId,
    List<String> grantedClientScopes,
    Long createdDate,
    Long lastUpdatedDate,
    List<String> grantedRealmRoles
) {}
