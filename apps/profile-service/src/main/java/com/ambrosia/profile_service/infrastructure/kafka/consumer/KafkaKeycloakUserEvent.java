package com.ambrosia.profile_service.infrastructure.kafka.consumer;

import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.ambrosia.library_core.dto.Topics;
import com.ambrosia.profile_service.infrastructure.kafka.dto.UserEvent;
import com.ambrosia.profile_service.user.api.dto.UserProjection;
import com.ambrosia.profile_service.user.infrastructure.UserProjectionService;
import com.ambrosia.profile_service.user.infrastructure.keycloak.service.KeycloakAdminClient;
import com.ambrosia.profile_service.user.infrastructure.keycloak.utils.KeycloakConfiguration;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

/**
 * Consumes Keycloak User Events {@link UserEvent} produced by user actions
 * and synchronizes local projections with the IdP state.
 */
@ConditionalOnBean(KeycloakAdminClient.class)
@RequiredArgsConstructor
@Component
public class KafkaKeycloakUserEvent {
    private final KeycloakConfiguration keycloakConfiguration;

    private final UserProjectionService userProjectionService;

    private final ObjectMapper objectMapper;


    @KafkaListener(
        topics = Topics.KEYCLOAK_EVENT,
        groupId = "profile-service",
        errorHandler = "serializationErrorHandler"
    )
    public void consumeMessage(@Payload byte[] message) throws Exception {
        var userEvent = objectMapper.readValue(message, UserEvent.class);
        if(!keycloakConfiguration.getRealm().equals(userEvent.realmName())){
            return;
        }
        switch (userEvent.type().toUpperCase()) {
            case "REGISTER" -> {
                userProjectionService.create(UserProjection.from(userEvent));
            }
            case "DELETE_ACCOUNT" -> {
                userProjectionService.delete(UUID.fromString(userEvent.userId()));
            }
            default -> {}
        };
    }
}
