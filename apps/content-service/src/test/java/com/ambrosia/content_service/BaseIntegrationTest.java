package com.ambrosia.content_service;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureInProcessTransport;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.ambrosia.content_service.config.KafkaTopics;
import com.ambrosia.library_core.ElasticIntegrationTest;
import com.ambrosia.library_core.KafkaIntegrationTest;
import com.ambrosia.library_core.PostgresRumIntegrationTest;
import com.ambrosia.library_core.RedisIntegrationTest;
import com.ambrosia.library_s3.S3IntegrationTest;
import com.ambrosia.library_s3.TestS3Configuration;


@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import({
    KafkaIntegrationTest.class,
    ElasticIntegrationTest.class,
    PostgresRumIntegrationTest.class,
    RedisIntegrationTest.class,
    S3IntegrationTest.class,
    KafkaTopics.class,
    TestS3Configuration.class
})
@AutoConfigureInProcessTransport
@TestInstance(Lifecycle.PER_CLASS)
@EnableScheduling
public abstract class BaseIntegrationTest {

    @DynamicPropertySource
    static public void props(DynamicPropertyRegistry registry){
        KafkaIntegrationTest.registerProperties(registry);
        PostgresRumIntegrationTest.registerProperties(registry);
        RedisIntegrationTest.registerProperties(registry);
        ElasticIntegrationTest.registerProperties(registry);
        registry.add("app.s3.base-prefix", () -> "files/post");
        registry.add("app.s3.temp-prefix", () -> "temp/files/post");
        S3IntegrationTest.registerProperties(registry);
    }
}