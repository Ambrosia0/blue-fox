package com.ambrosia.community_service;

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

import com.ambrosia.community_service.config.KafkaTopics;
import com.ambrosia.community_service.utils.CommunityCreator;
import com.ambrosia.community_service.utils.FollowCreator;
import com.ambrosia.community_service.utils.ScopeLinkCreator;
import com.ambrosia.community_service.utils.UserBanCreator;
import com.ambrosia.library_core.ElasticIntegrationTest;
import com.ambrosia.library_core.KafkaIntegrationTest;
import com.ambrosia.library_core.PostgresIntegrationTest;
import com.ambrosia.library_core.RedisIntegrationTest;
import com.ambrosia.library_s3.S3IntegrationTest;


@AutoConfigureInProcessTransport
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EnableScheduling
@TestInstance(Lifecycle.PER_CLASS)
@Import({
    ScopeLinkCreator.class,
    CommunityCreator.class,
    FollowCreator.class,
    UserBanCreator.class,
    KafkaIntegrationTest.class,
    PostgresIntegrationTest.class,
    RedisIntegrationTest.class,
    ElasticIntegrationTest.class,
    S3IntegrationTest.class,
    KafkaTopics.class
})
public abstract class BaseIntegrationTest {
    @DynamicPropertySource
    static public void props(DynamicPropertyRegistry registry){
        KafkaIntegrationTest.registerProperties(registry);
        PostgresIntegrationTest.registerProperties(registry);
        RedisIntegrationTest.registerProperties(registry);
        ElasticIntegrationTest.registerProperties(registry);
        S3IntegrationTest.registerProperties(registry);
        registry.add("app.s3.base-prefix", () -> "avatars/user");
        registry.add("app.s3.temp-prefix", () -> "temp/avatars/user");
    }
}