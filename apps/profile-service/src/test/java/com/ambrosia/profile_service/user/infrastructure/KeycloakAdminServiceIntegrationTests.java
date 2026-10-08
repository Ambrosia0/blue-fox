package com.ambrosia.profile_service.user.infrastructure;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ambrosia.profile_service.BaseIntegrationTest;
import com.ambrosia.profile_service.exception.api.user.UserDoesntBannedException;
import com.ambrosia.profile_service.exception.api.user.UserDoesntExistException;
import com.ambrosia.profile_service.exception.api.user.UserIsDisabledException;
import com.ambrosia.profile_service.user.application.UserProfileService;
import com.ambrosia.profile_service.user.infrastructure.keycloak.service.impl.KeycloakAdminService;
import com.ambrosia.profile_service.user.infrastructure.persistence.JdbcUserRepository;
import com.ambrosia.profile_service.util.UserCreator;

public class KeycloakAdminServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired JdbcUserRepository userRepository;
    @Autowired UserProfileService userProfileService;

    @Autowired KeycloakAdminService keycloakAdminService;

    @Autowired UserCreator userCreator;
    

    @Test
    void shouldThrowUserDoesntExistExceptionOnBanUser(){
        assertThrows(
            UserDoesntExistException.class,
            () -> keycloakAdminService.banUser(UUID.randomUUID())
        );
    }

    @Test
    void shouldBanUser(){
        var user = userCreator.create();
        assertDoesNotThrow(() -> keycloakAdminService.banUser(user.getId()));
        await().atMost(Duration.ofSeconds(6))
            .untilAsserted(() -> assertFalse(userRepository.findByUsernameIgnoreCase(user.getUsername()).get().isEnabled()));
    }

    @Test
    void shouldThrowUserIsDisabledException(){
        var user = userCreator.create();
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
        var user = userCreator.create();
        assertThrows(
            UserDoesntBannedException.class,
            () -> keycloakAdminService.unbanUser(user.getId())
        );
    }

    @Test
    void shouldUnbanUser(){
        var user = userCreator.create();
        assertDoesNotThrow(() -> keycloakAdminService.banUser(user.getId()));
        await().atMost(Duration.ofSeconds(6))
            .untilAsserted(() -> assertFalse(userRepository.findByUsernameIgnoreCase(user.getUsername()).get().isEnabled()));
        assertDoesNotThrow(() -> keycloakAdminService.unbanUser(user.getId()));
        await().atMost(Duration.ofSeconds(6))
            .untilAsserted(() -> assertTrue(userRepository.findByUsernameIgnoreCase(user.getUsername()).get().isEnabled()));
    }
}
