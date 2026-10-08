package com.ambrosia.collaboration_service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.socket.WebSocketSession;

import com.ambrosia.collaboration_service.utils.TestBinaryWebSocketHandler;
import com.ambrosia.collaboration_service.utils.WebSocketConnectionFactory;
import com.ambrosia.library_core.KafkaIntegrationTest;

@ActiveProfiles("test")
@Import({KafkaIntegrationTest.class, TestSecurityConfiguration.class})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BaseIntegrationTest {

    @LocalServerPort 
    protected int port;

    @DynamicPropertySource 
    static public void props(DynamicPropertyRegistry registry){
        KafkaIntegrationTest.registerProperties(registry);
    }

    protected CompletableFuture<WebSocketSession> createSession(UUID userId, Long postId){
        return WebSocketConnectionFactory.createClient().execute(
            new TestBinaryWebSocketHandler(),
            WebSocketConnectionFactory.createHeaders(userId), 
            WebSocketConnectionFactory.buildUri(postId, port)
        );
    }
}
