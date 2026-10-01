package com.ambrosia.profile_service;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.ambrosia.profile_service.kafka.consumer.KafkaKeycloakAdminEvent;
import com.ambrosia.profile_service.kafka.consumer.KafkaKeycloakUserEvent;
import com.ambrosia.profile_service.keycloak.service.KeycloakAdminClient;
import com.ambrosia.profile_service.user.model.dto.request.RegisterRequest;
import com.ambrosia.profile_service.user.repository.UserRepository;
import com.ambrosia.profile_service.user.service.UserProfileService;
import com.ambrosia.profile_service.util.Factory;

import tools.jackson.databind.ObjectMapper;

@ActiveProfiles(profiles = "es-disabled", inheritProfiles = true)
class KeycloakUserRegistrationTest extends BaseIntegrationTest{
    @MockitoSpyBean KafkaKeycloakUserEvent kafkaKeycloakEvent;
    @MockitoSpyBean KafkaKeycloakAdminEvent kafkaKeycloakAdminEvent;

    @Autowired ObjectMapper objectMapper;
    @Autowired UserProfileService userService;
    @Autowired UserRepository userRepository;
    @Autowired KeycloakAdminClient keycloakService;

    @Autowired UserRegistration userRegistration;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;


    public final static RegisterRequest registerRequest = 
        new RegisterRequest("TestUsername", "testtest", "testtest", "testpassword", "testemail@testemail.com");
    
    @Test
    void shouldNotThrowException() throws Exception{
        var user = Factory.createUser();
        userRegistration.register(user);
        await().atMost(Duration.ofSeconds(20)).pollDelay(Duration.ofSeconds(5))
            .untilAsserted(() -> 
                assertDoesNotThrow(() -> {
                    var created = userRepository.findByUsernameIgnoreCase(user.getUsername()).get();
                    var keycloakUser = keycloakService.get(created.getId()).get();
                    keycloakUser.setEmailVerified(true);
                    keycloakUser.getRequiredActions().removeFirst();
                    keycloakService.update(keycloakUser);
                })
            );
    }

    @AfterAll
    void cleanUp(){
        userRepository.deleteAll();
    }

}
