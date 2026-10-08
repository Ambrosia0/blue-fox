package com.ambrosia.profile_service;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.ambrosia.profile_service.user.api.dto.request.RegisterRequest;
import com.ambrosia.profile_service.user.application.UserProfileService;
import com.ambrosia.profile_service.user.domain.entity.User;
import com.ambrosia.profile_service.user.infrastructure.keycloak.service.KeycloakAdminClient;
import com.ambrosia.profile_service.user.infrastructure.persistence.JdbcUserRepository;
import com.ambrosia.profile_service.util.UserCreator;

@ActiveProfiles(profiles = "es-disabled", inheritProfiles = true)
class KeycloakUserRegistrationTest extends BaseIntegrationTest{
    @Autowired UserProfileService userService;
    @Autowired JdbcUserRepository userRepository;
    @Autowired KeycloakAdminClient keycloakService;

    @Autowired UserRegistration userRegistration;

    @Autowired UserCreator userCreator;

    public final static RegisterRequest registerRequest = 
        new RegisterRequest("TestUsername", "testtest", "testtest", "testpassword", "testemail@testemail.com");
    
    @Test
    void shouldNotThrowException() throws Exception{
        var user = User.builder()
            .id(UUID.randomUUID())
            .username("testUsername")
            .firstName("testfirstName")
            .lastName("testLastName")
            .email("test@test.com")
            .isEnabled(true)
            .password("testPassword")
            .build();
        userRegistration.register(user);
        await().atMost(Duration.ofSeconds(20)).pollInterval(Duration.ofSeconds(2))
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
        userCreator.cleanUp();
    }

}
