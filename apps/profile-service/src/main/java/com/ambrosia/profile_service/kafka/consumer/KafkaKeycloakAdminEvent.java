package com.ambrosia.profile_service.kafka.consumer;

import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.ambrosia.library_core.dto.Topics;
import com.ambrosia.profile_service.kafka.dto.AdminEvent;
import com.ambrosia.profile_service.keycloak.dto.UserRepresentation;
import com.ambrosia.profile_service.keycloak.service.KeycloakAdminClient;
import com.ambrosia.profile_service.keycloak.utils.KeycloakConfiguration;
import com.ambrosia.profile_service.user.model.dto.UserProjection;
import com.ambrosia.profile_service.user.service.UserProjectionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

/**
 * Consumes Keycloak Admin Events produced by administrative actions {@link AdminEvent}
 * and synchronizes local projections with the IdP state.
 */
@ConditionalOnBean(KeycloakAdminClient.class)
@RequiredArgsConstructor
@Slf4j
@Component
public class KafkaKeycloakAdminEvent {
    private final KeycloakConfiguration keycloakConfiguration;

    private final ObjectMapper objectMapper;

    private final UserProjectionService userProjectionService;

    @KafkaListener(
        topics = Topics.KEYCLOAK_EVENT_ADMIN,
        groupId = "profile-service",
        errorHandler = "serializationErrorHandler")
    public void consumeMessage(@Payload byte[] message) throws Exception {
        var event = objectMapper.readValue(message, AdminEvent.class);
        if(event.realmName().equals(keycloakConfiguration.getRealm()) 
            && event.resourceType().equals("USER")){
            var userInfo = objectMapper.readValue(event.representation(), UserRepresentation.class);
            switch (event.operationType()) {
                case CREATE ->{
                    log.debug("ADMIN EVENT CREATE: {}", userInfo.toString());
                    userProjectionService.create(
                        UserProjection.from(
                            extractUUID(event.resourcePath()), 
                            userInfo
                        )
                    );
                }
                case UPDATE ->{
                    log.debug("ADMIN EVENT UPDATE: {}", userInfo.toString());
                    userProjectionService.update(UserProjection.from(
                        UUID.fromString(userInfo.getId()), 
                        userInfo
                    ));
                }
                case DELETE ->{
                    userProjectionService.delete(extractUUID(event.resourcePath()));
                }
                default ->{
                    log.info("Unsupported operation type received on admin event consumer! {}", event.operationType());
                }
            }
        }else{
            log.warn("Consumed message from another realm/wrong resource type! {}", event.toString());
        }
    }

    private UUID extractUUID(String resourcePath){
        var prefix = "users/";
        int start = resourcePath.indexOf("users/");
        if(start == -1)
            return null;

        start += prefix.length();
        var end = resourcePath.indexOf("/", start);
        return UUID.fromString(
            end == -1?
                resourcePath.substring(start):
                resourcePath.substring(start, end)
        );
    }
}
