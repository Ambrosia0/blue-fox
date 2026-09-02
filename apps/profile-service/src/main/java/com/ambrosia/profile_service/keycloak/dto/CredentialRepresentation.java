package com.ambrosia.profile_service.keycloak.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;

/**
 * @see "https://www.keycloak.org/docs-api/latest/rest-api/index.html#CredentialRepresentation"
 */
@Builder
public record CredentialRepresentation(
    @JsonProperty("id")
    @JsonInclude(value = Include.NON_NULL)
    String id,

    @JsonProperty("type")
    @JsonInclude(value = Include.NON_NULL)
    String type,

    @JsonProperty("userLabel")
    @JsonInclude(value = Include.NON_NULL)
    String userLabel,

    @JsonProperty("createdDate")
    @JsonInclude(value = Include.NON_NULL)
    Long createdDate,

    @JsonProperty("secretData")
    @JsonInclude(value = Include.NON_NULL)
    String secretData,

    @JsonProperty("credentialData")
    @JsonInclude(value = Include.NON_NULL)
    String credentialData,

    @JsonProperty("value")
    @JsonInclude(value = Include.NON_NULL)
    String value,

    @JsonProperty("temporary")
    @JsonInclude(value = Include.NON_NULL)
    Boolean temporary,

    @JsonProperty("device")
    @JsonInclude(value = Include.NON_NULL)
    String device,

    @JsonProperty("hashedSaltedValue")
    @JsonInclude(value = Include.NON_NULL)
    String hashedSaltedValue,

    @JsonProperty("salt")
    @JsonInclude(value = Include.NON_NULL)
    String salt,

    @JsonProperty("algorithm")
    @JsonInclude(value = Include.NON_NULL)
    String algorithm
) {}
