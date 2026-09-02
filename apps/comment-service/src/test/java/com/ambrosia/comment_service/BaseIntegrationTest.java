package com.ambrosia.comment_service;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.ambrosia.comment_service.utils.CommentCreator;
import com.ambrosia.comment_service.utils.CommunityBanCreator;
import com.ambrosia.comment_service.utils.CommunityCreator;
import com.ambrosia.comment_service.utils.CommunityFollowCreator;
import com.ambrosia.comment_service.utils.PostProjectionCreator;
import com.ambrosia.library_core.KafkaIntegrationTest;
import com.ambrosia.library_core.PostgresIntegrationTest;
import com.ambrosia.library_core.RedisIntegrationTest;
import com.ambrosia.library_s3.S3IntegrationTest;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
@Import({
    KafkaIntegrationTest.class,
    PostgresIntegrationTest.class,
    RedisIntegrationTest.class,
    S3IntegrationTest.class,
    CommunityCreator.class, 
    CommentCreator.class, 
    PostProjectionCreator.class,
    CommunityBanCreator.class,
    CommunityFollowCreator.class
})
public abstract class BaseIntegrationTest {

    @DynamicPropertySource
    static public void props(DynamicPropertyRegistry registry){
        KafkaIntegrationTest.registerProperties(registry);
        PostgresIntegrationTest.registerProperties(registry);
        RedisIntegrationTest.registerProperties(registry);
        S3IntegrationTest.registerProperties(registry);
    }
}
