package com.ambrosia.profile_service.keycloak.utils;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.web.client.RestClient;


@ImportRuntimeHints(KeycloakHints.class)
@EnableConfigurationProperties(KeycloakConfiguration.class)
@Configuration
public class KeycloakConfig {
    @Qualifier("keycloakRestClient")
    @Bean
    RestClient keycloakRestClient(RestClient.Builder builder, KeycloakConfiguration appConfiguration){
        return builder
            .baseUrl(appConfiguration.getBaseUrl())
            .build();
    }
}
