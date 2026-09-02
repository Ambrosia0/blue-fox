package com.ambrosia.profile_service.keycloak.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;



@JsonIgnoreProperties(ignoreUnknown = true)
public record TokenResponse(
    @JsonProperty("access_token")
    String accessToken,

    @JsonProperty("expires_in")
    int expiresIn,

    @JsonProperty("refresh_expires_in")
    int refreshExpiresIn,

    @JsonProperty("token_type")
    String tokenType,

    @JsonProperty("not-before-policy")
    int notBeforePolicy
){}
