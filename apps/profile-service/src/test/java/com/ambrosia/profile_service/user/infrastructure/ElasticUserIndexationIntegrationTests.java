package com.ambrosia.profile_service.user.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import com.ambrosia.outbox.elastic.ElasticsearchOutboxRelay;
import com.ambrosia.profile_service.BaseIntegrationTest;
import com.ambrosia.profile_service.user.application.query.UserSearchService;
import com.ambrosia.profile_service.user.domain.entity.User;
import com.ambrosia.profile_service.user.infrastructure.elastic.ElasticUserRepository;
import com.ambrosia.profile_service.user.infrastructure.entity.ElasticUser;
import com.ambrosia.profile_service.user.infrastructure.persistence.JdbcUserRepository;
import com.ambrosia.profile_service.util.UserCreator;

public class ElasticUserIndexationIntegrationTests extends BaseIntegrationTest {
    @Autowired ElasticsearchOperations elasticsearchOperations;

    @Autowired ElasticUserRepository elasticUserRepository;

    @Autowired UserSearchService userSearchService;

    @Autowired ElasticsearchOutboxRelay elasticsearchOutboxRelay;

    @Autowired JdbcUserRepository userRepository;

    @Autowired UserCreator userCreator;

    @BeforeAll
    void init(){
        assertTrue(elasticsearchOperations.indexOps(ElasticUser.class).exists());
        elasticUserRepository.deleteAll();
        elasticsearchOperations.indexOps(ElasticUser.class).refresh();
    }

    @Test
    void shouldReturnUserInfo(){
        var created = List.of(
            userCreator.create(),
            userCreator.create(),
            userCreator.create()
        )
            .stream()
            .map(User::getId)
            .collect(Collectors.toSet());
        elasticsearchOutboxRelay.flush();
        assertEquals(created.size(), userSearchService.search("test", 10)
            .stream()
            .filter(t -> created.contains(t.id()))
            .count()
        );
    }
}