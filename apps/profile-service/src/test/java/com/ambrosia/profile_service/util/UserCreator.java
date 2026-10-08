package com.ambrosia.profile_service.util;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.web.server.ResponseStatusException;

import com.ambrosia.profile_service.UserRegistration;
import com.ambrosia.profile_service.user.domain.entity.User;
import com.ambrosia.profile_service.user.infrastructure.elastic.ElasticUserRepository;
import com.ambrosia.profile_service.user.infrastructure.keycloak.service.KeycloakAdminClient;
import com.ambrosia.profile_service.user.infrastructure.persistence.JdbcUserRepository;
import com.ambrosia.profile_service.user.utils.Role;

@TestComponent
public class UserCreator {
    @Autowired JdbcUserRepository userRepository;

    @Autowired UserRegistration userRegistration;

    @Autowired KeycloakAdminClient keycloakAdminClient;

    @Autowired(required = false) ElasticUserRepository elasticUserRepository;

    public User create(){
        var user = User.builder()
            .id(UUID.randomUUID())
            .username("TestUsername"+ThreadLocalRandom.current().nextLong())
            .email("testEmail"+ThreadLocalRandom.current().nextLong()+"@test.com")
            .password("testPassword")
            .firstName("firstName")
            .isEnabled(true)
            .lastName("lastName")
            .role(Role.user)
            .build();
        userRegistration.register(user);
        await()
            .atMost(Duration.ofSeconds(6))
            .pollInterval(Duration.ofSeconds(1))
            .untilAsserted(
                () -> assertTrue(userRepository
                    .findByUsernameIgnoreCase(user.getUsername()).isPresent()
                )
            );
        return userRepository.findByUsernameIgnoreCase(user.getUsername()).get();
    }

    public void cleanUp(){
        var it = userRepository.findAll().iterator();
        while(it.hasNext()){
            var user = it.next();
            try {
                keycloakAdminClient.delete(user.getId());
            } catch (ResponseStatusException e) {
                // TODO: handle exception
            }
        }
        userRepository.deleteAll();
        if(elasticUserRepository != null)
           elasticUserRepository.deleteAll();
    }
}
