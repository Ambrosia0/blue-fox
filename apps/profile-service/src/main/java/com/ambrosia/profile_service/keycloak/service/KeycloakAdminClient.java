package com.ambrosia.profile_service.keycloak.service;

import java.util.Optional;
import java.util.UUID;

import com.ambrosia.profile_service.keycloak.dto.MappingsRepresentation;
import com.ambrosia.profile_service.keycloak.dto.UserRepresentation;

/**
 * Basic REST client service for interacting with Keycloak Admin REST API
 * 
 * <p>Provides basic functionality for performing CRUD operations with users</p>
 * 
 * @see <a href="https://www.keycloak.org/docs-api/latest/rest-api/">Keycloak Admin REST API</a>
 */
public interface KeycloakAdminClient {
    void register(UserRepresentation userRepresentation);
    void update(UserRepresentation userRepresentation);
    void delete(UUID userId);
    Optional<UserRepresentation> get(UUID userId);
    Optional<MappingsRepresentation> getRoleMappings(UUID userId);
}
