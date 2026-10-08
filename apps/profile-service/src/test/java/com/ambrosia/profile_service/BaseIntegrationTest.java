package com.ambrosia.profile_service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureInProcessTransport;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.ambrosia.library_core.ElasticIntegrationTest;
import com.ambrosia.library_core.KeycloakIntegrationTest;
import com.ambrosia.library_core.PostgresIntegrationTest;
import com.ambrosia.library_core.RedisIntegrationTest;
import com.ambrosia.library_s3.S3IntegrationTest;
import com.ambrosia.profile_service.config.KafkaTopics;
import com.ambrosia.profile_service.util.TestKeycloakRestClient;
import com.ambrosia.profile_service.util.UserCreator;

@AutoConfigureMockMvc
@AutoConfigureInProcessTransport
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EnableScheduling
@TestInstance(Lifecycle.PER_CLASS)
@Import({
    KafkaTopics.class, 
    UserRegistration.class, 
    UserCreator.class,
    KeycloakIntegrationTest.class,
    PostgresIntegrationTest.class,
    RedisIntegrationTest.class,
    ElasticIntegrationTest.class,
    S3IntegrationTest.class,
    TestKeycloakRestClient.class
})
public abstract class BaseIntegrationTest {
    @Autowired UserCreator userCreator;
    
    @DynamicPropertySource
    static public void props(DynamicPropertyRegistry registry){
        KeycloakIntegrationTest.registerProperties(registry);
        PostgresIntegrationTest.registerProperties(registry);
        RedisIntegrationTest.registerProperties(registry);
        ElasticIntegrationTest.registerProperties(registry);
        S3IntegrationTest.registerProperties(registry);
        registry.add("app.s3.base-prefix", () -> "avatars/user");
        registry.add("app.s3.temp-prefix", () -> "temp/avatars/user");
    }
    
    @AfterAll 
    void cleanUp(){
        userCreator.cleanUp();
    }
}