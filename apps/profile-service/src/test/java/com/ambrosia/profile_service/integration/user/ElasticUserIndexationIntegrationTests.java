package com.ambrosia.profile_service.integration.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import com.ambrosia.outbox.elastic.ElasticsearchOutboxRelay;
import com.ambrosia.outbox.kafka.KafkaOutboxRelay;
import com.ambrosia.profile_service.BaseIntegrationTest;
import com.ambrosia.profile_service.kafka.consumer.KafkaKeycloakUserEvent;
import com.ambrosia.profile_service.kafka.dto.UserEvent;
import com.ambrosia.profile_service.kafka.dto.UserEvent.UserDetails;
import com.ambrosia.profile_service.user.model.entity.User;
import com.ambrosia.profile_service.user.model.entity.elastic.ElasticUser;
import com.ambrosia.profile_service.user.repository.UserRepository;
import com.ambrosia.profile_service.user.repository.elastic.ElasticUserRepository;
import com.ambrosia.profile_service.user.service.UserProfileService;
import com.ambrosia.profile_service.user.service.UserSearchService;
import com.ambrosia.profile_service.util.Factory;

import tools.jackson.databind.ObjectMapper;

public class ElasticUserIndexationIntegrationTests extends BaseIntegrationTest {
    @Autowired ElasticsearchOperations elasticsearchOperations;

    @Autowired UserProfileService userService;

    @Autowired KafkaKeycloakUserEvent kafkaEventConsumer;

    @Autowired ElasticUserRepository elasticUserRepository;

    @Autowired UserSearchService userSearchService;

    @Autowired ObjectMapper objectMapper;

    @Autowired KafkaOutboxRelay kafkaOutboxRelay;

    @Autowired ElasticsearchOutboxRelay elasticsearchOutboxRelay;

    @Autowired UserRepository userRepository;

    @Value("${app.keycloak.realm}")
    private String realm;

    @BeforeAll
    void init(){
        assertTrue(elasticsearchOperations.indexOps(ElasticUser.class).exists());
        elasticUserRepository.deleteAll();
        elasticsearchOperations.indexOps(ElasticUser.class).refresh();
    }

    @Test
    void shouldReturnUserInfo(){
        createUser();
        createUser();
        createUser();
        elasticsearchOutboxRelay.flush();
        assertEquals(3, userSearchService.search("test", 10).size());
    }

    private User createUser(){
        try {
            var user = Factory.createUser();
            var userEvent = new UserEvent(
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
            kafkaEventConsumer.consumeMessage(objectMapper.writeValueAsBytes(userEvent));
            kafkaOutboxRelay.flush();
            elasticsearchOperations.indexOps(ElasticUser.class).refresh();
            return user;
        } catch (Exception e) {
            throw new RuntimeException("error", e);
        }
    }

    @AfterEach
    void cleanUp() {
        elasticUserRepository.deleteAll();
        elasticsearchOperations.indexOps(ElasticUser.class).refresh();
        userRepository.deleteAll();
    }
}