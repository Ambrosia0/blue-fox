package com.ambrosia.profile_service.user.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.profile_service.BaseIntegrationTest;
import com.ambrosia.profile_service.user.application.query.UserSearchService;
import com.ambrosia.profile_service.user.domain.entity.User;
import com.ambrosia.profile_service.util.UserCreator;

@Transactional
@ActiveProfiles(profiles = "es-disabled", inheritProfiles = true)
public class PostgresUserSearchIntegrationTests extends BaseIntegrationTest {
    @Autowired UserSearchService userSearchService;

    @Autowired UserCreator userCreator;

    @Value("${app.keycloak.realm}")
    private String realm;

    @Test
    void shouldReturnUserInfo(){
        var created = List.of(
            userCreator.create(),
            userCreator.create()
        )
            .stream()
            .map(User::getId)
            .collect(Collectors.toSet());
        assertEquals(
            created.size(), 
            userSearchService.search("test", 10)
                .stream()
                .filter(t -> created.contains(t.id()))
                .count()
        );
    }

}