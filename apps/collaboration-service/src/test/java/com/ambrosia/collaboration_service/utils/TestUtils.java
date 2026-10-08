package com.ambrosia.collaboration_service.utils;

import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.awaitility.core.ThrowingRunnable;

import com.ambrosia.content_service.grpc.CollaborationContentResponse;
import com.ambrosia.content_service.grpc.User;

public class TestUtils {
    public static void awaitAssertion(ThrowingRunnable throwingRunnable) {
        await()
            .atMost(Duration.ofSeconds(2))
            .pollDelay(Duration.ofMillis(500))
            .untilAsserted(throwingRunnable);
    }

    public static void awaitAssertion(ThrowingRunnable throwingRunnable, Duration duration) {
        await()
            .atMost(duration)
            .pollDelay(Duration.ofMillis(500))
            .untilAsserted(throwingRunnable);
    }

    public static User createUser(){
        return User.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setUsername("name")
            .setFirstName("name")
            .setLastName("name")
            .build();
    }

    public static Optional<CollaborationContentResponse> buildResponse(Long postId, User author, User... collaborators){
        return Optional.of(CollaborationContentResponse.newBuilder()
                .setAuthor(author)
                .setContent("TestContent")
                .setPostId(postId)
                .setLastUpdate(Instant.now().minus(5, TimeUnit.HOURS.toChronoUnit()).toEpochMilli())
                .addAllCollaborationUsers(Arrays.asList(collaborators))
                .build()
            );
    }
}
