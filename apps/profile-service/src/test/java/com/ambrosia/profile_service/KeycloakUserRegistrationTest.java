package com.ambrosia.profile_service;

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
        Thread.sleep(4000);
        user = userRepository.findByUsernameIgnoreCase(user.getUsername())
            .orElseThrow(() -> new Exception("User doesn't created!"));
        var keycloakUser = keycloakService.get(user.getId())
            .orElseThrow(() -> new Exception("User doesn't exist!"));
        keycloakUser.setEmailVerified(true);
        keycloakUser.getRequiredActions().removeFirst();
        keycloakService.update(keycloakUser);
    }

    @AfterAll
    void cleanUp(){
        userRepository.deleteAll();
    }

}
