package com.ambrosia.profile_service.integration.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.profile_service.BaseIntegrationTest;
import com.ambrosia.profile_service.kafka.consumer.KafkaKeycloakUserEvent;
import com.ambrosia.profile_service.kafka.dto.UserEvent;
import com.ambrosia.profile_service.kafka.dto.UserEvent.UserDetails;
import com.ambrosia.profile_service.user.model.entity.User;
import com.ambrosia.profile_service.user.service.UserProfileService;
import com.ambrosia.profile_service.user.service.UserSearchService;
import com.ambrosia.profile_service.util.Factory;

import tools.jackson.databind.ObjectMapper;

@Transactional
@ActiveProfiles(profiles = "es-disabled", inheritProfiles = true)
public class PostgresUserSearchIntegrationTests extends BaseIntegrationTest {
    @Autowired UserProfileService userService;

    @Autowired KafkaKeycloakUserEvent kafkaEventConsumer;

    @MockitoSpyBean KafkaKeycloakUserEvent kafkaKeycloakEvent;

    @Autowired UserSearchService userSearchService;

    @Autowired ObjectMapper objectMapper;

    @Value("${app.keycloak.realm}")
    private String realm;

    @Test
    void shouldReturnUserInfo(){
        createUser();
        createUser();
        assertEquals(2, userSearchService.search("test", 10).size());
    }

    private User createUser(){
        try {
            var user = Factory.createUser();
            var event = new UserEvent(
                user.getId().toString(),
                realm,
                "REGISTER",
                new UserDetails(
                    user.getEmail(), 
                    user.getUsername(), 
                    user.getFirstName(), 
                    user.getLastName(),
                    true,
                    null
                )
            );
            kafkaEventConsumer.consumeMessage(objectMapper.writeValueAsBytes(event));
            return user;
        } catch (Exception e) {
            throw new RuntimeException("error", e);
        }
    }
    
    @AfterEach
    void cleanUp() {}
}