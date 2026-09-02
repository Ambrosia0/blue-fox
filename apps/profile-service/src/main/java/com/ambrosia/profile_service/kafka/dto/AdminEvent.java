package com.ambrosia.profile_service.kafka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @see https://github.com/keycloak/keycloak/blob/main/server-spi-private/src/main/java/org/keycloak/events/admin/AdminEvent.java
 */
public record AdminEvent(
    String realmId,
    String realmName,
    String resourceType,
    String resourcePath,

    // could be turned off in keycloak settings
    String representation,

    @JsonProperty("operationType")
    OperationType operationType
) {
    /**
     * @see https://github.com/keycloak/keycloak/blob/main/server-spi-private/src/main/java/org/keycloak/events/admin/OperationType.java
     */
    public enum OperationType{
        ACTION,
        CREATE,
        DELETE,
        UPDATE
    }

    // public record AdminUserRepresentation(
    //     UUID id, // representation id in create event is fake
    //     String username,
    //     String firstName,
    //     String lastName,
    //     String email,
    //     List<String> realmRoles
    // ){}
}
