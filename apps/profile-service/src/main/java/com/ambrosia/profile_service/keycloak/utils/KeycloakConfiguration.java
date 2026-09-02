package com.ambrosia.profile_service.keycloak.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@ConfigurationProperties(prefix = "app.keycloak")
public class KeycloakConfiguration{
    private final long authKeyRotationTime;
    private final String realm;
    private final String secret;
    private final String clientId;
    private final String baseUrl;
}