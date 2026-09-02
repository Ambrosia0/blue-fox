package com.ambrosia.profile_service.integration.user;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.ambrosia.profile_service.BaseIntegrationTest;
import com.ambrosia.profile_service.UserRegistration;
import com.ambrosia.profile_service.exception.api.user.UserDoesntBannedException;
import com.ambrosia.profile_service.exception.api.user.UserDoesntExistException;
import com.ambrosia.profile_service.exception.api.user.UserIsDisabledException;
import com.ambrosia.profile_service.keycloak.service.KeycloakAdminClient;
import com.ambrosia.profile_service.keycloak.service.impl.KeycloakAdminService;
import com.ambrosia.profile_service.user.model.entity.User;
import com.ambrosia.profile_service.user.repository.UserRepository;
import com.ambrosia.profile_service.user.repository.elastic.ElasticUserRepository;
import com.ambrosia.profile_service.user.service.UserProfileService;
import com.ambrosia.profile_service.util.Factory;

@Import(BaseIntegrationTest.TestKeycloakConfiguration.class)
public class KeycloakAdminServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired UserRepository userRepository;
    @Autowired UserRegistration userRegistration;
    @Autowired ElasticUserRepository elasticUserRepository;
    @Autowired UserProfileService userProfileService;

    @Autowired KeycloakAdminClient keycloakAdminClient;
    @Autowired KeycloakAdminService keycloakAdminService;
    
    @AfterAll
    void cleanUp(){
        var it = userRepository.findAll().iterator();
        while(it.hasNext()){
            var user = it.next();
            keycloakAdminClient.delete(user.getId());
        }
        userRepository.deleteAll();
        elasticUserRepository.deleteAll();
    }

    @Test
    void shouldThrowUserDoesntExistExceptionOnBanUser(){
        assertThrows(
            UserDoesntExistException.class,
            () -> keycloakAdminService.banUser(UUID.randomUUID())
        );
    }

    @Test
    void shouldBanUser(){
        var user = registerUser();
        assertDoesNotThrow(() -> keycloakAdminService.banUser(user.getId()));
        await().atMost(Duration.ofSeconds(6))
            .untilAsserted(() -> assertFalse(userRepository.findByUsernameIgnoreCase(user.getUsername()).get().isEnabled()));
    }

    @Test
    void shouldThrowUserIsDisabledException(){
        var user = registerUser();
        assertDoesNotThrow(() -> keycloakAdminService.banUser(user.getId()));
        assertThrows(
            UserIsDisabledException.class, 
            () -> keycloakAdminService.banUser(user.getId())
        );
    }

    @Test
    void shouldThrowUserDoesntExistExceptionOnUnbanUser(){
        assertThrows(
            UserDoesntExistException.class,
            () -> keycloakAdminService.unbanUser(UUID.randomUUID())
        );
    }

    @Test
    void shouldThrowUserDoesntBannedException(){
        var user = registerUser();
        assertThrows(
            UserDoesntBannedException.class,
            () -> keycloakAdminService.unbanUser(user.getId())
        );
    }

    @Test
    void shouldUnbanUser(){
        var user = registerUser();
        assertDoesNotThrow(() -> keycloakAdminService.banUser(user.getId()));
        await().atMost(Duration.ofSeconds(6))
            .untilAsserted(() -> assertFalse(userRepository.findByUsernameIgnoreCase(user.getUsername()).get().isEnabled()));
        assertDoesNotThrow(() -> keycloakAdminService.unbanUser(user.getId()));
        await().atMost(Duration.ofSeconds(6))
            .untilAsserted(() -> assertTrue(userRepository.findByUsernameIgnoreCase(user.getUsername()).get().isEnabled()));
    }


    private User registerUser(){
        try {
            var user = Factory.createUser();
            userRegistration.register(user);
            await().atMost(Duration.ofSeconds(6)).untilAsserted(() -> assertTrue(userRepository.findByUsernameIgnoreCase(user.getUsername()).isPresent()));
            return userRepository.findByUsernameIgnoreCase(user.getUsername()).get();
        } catch (Exception e) {
            throw new RuntimeException("Can't register user!", e);
        }
    }
}
