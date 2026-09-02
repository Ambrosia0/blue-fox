package com.ambrosia.report_service;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureInProcessTransport;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.ambrosia.library_core.KafkaIntegrationTest;
import com.ambrosia.library_core.PostgresIntegrationTest;
import com.ambrosia.library_core.RedisIntegrationTest;
import com.ambrosia.report_service.config.KafkaTopics;

@AutoConfigureMockMvc
@AutoConfigureInProcessTransport
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EnableScheduling
@TestInstance(Lifecycle.PER_CLASS)
@Import({
    PostgresIntegrationTest.class,
    KafkaIntegrationTest.class,
    RedisIntegrationTest.class,
    KafkaTopics.class
})
public abstract class BaseIntegrationTest {
    @Autowired KafkaTemplate<Integer, byte[]> kafkaTemplate;

    @DynamicPropertySource
    static public void props(DynamicPropertyRegistry registry){
        KafkaIntegrationTest.registerProperties(registry);
        PostgresIntegrationTest.registerProperties(registry);
        RedisIntegrationTest.registerProperties(registry);
    }
}