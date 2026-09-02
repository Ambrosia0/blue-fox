package com.ambrosia.profile_service;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureInProcessTransport;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;

import com.ambrosia.library_core.ElasticIntegrationTest;
import com.ambrosia.library_core.KeycloakIntegrationTest;
import com.ambrosia.library_core.PostgresIntegrationTest;
import com.ambrosia.library_core.RedisIntegrationTest;
import com.ambrosia.library_s3.S3IntegrationTest;
import com.ambrosia.profile_service.config.KafkaTopics;
import com.ambrosia.profile_service.keycloak.utils.KeycloakConfiguration;
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
    S3IntegrationTest.class
})
public abstract class BaseIntegrationTest {
    @Autowired KafkaTemplate<Integer, byte[]> kafkaTemplate;
    
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
    @TestConfiguration
    public static class TestKeycloakConfiguration{
        private static final Logger log = LoggerFactory.getLogger(TestKeycloakConfiguration.class);

        @Bean
        @Primary
        RestClient keycloakRestClient(KeycloakConfiguration appConfiguration){
            return RestClient.builder()
                .requestFactory(new BufferingClientHttpRequestFactory(
                    new SimpleClientHttpRequestFactory()
                ))
                .requestInterceptor((req, body, execution) ->{
                    log.debug("Rest client request: {} {}", req.getMethod(), req.getURI());
                    if(body.length > 0)
                        log.debug("Rest client request: body {}", new String(body));
                    var resp = execution.execute(req, body);
                    var respBody = StreamUtils.copyToString(
                        resp.getBody(), 
                        StandardCharsets.UTF_8
                    );
                    log.debug("Rest client response: {}", resp.getStatusCode());
                    log.debug("Rest client response: body {}", respBody);
                    return resp; 
                })
                .baseUrl(appConfiguration.getBaseUrl())
                .build();
        }
    }
    
}