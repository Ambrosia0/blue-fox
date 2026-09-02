package org.ambrosia;

import org.ambrosia.notification_service.controller.AsyncNotificationController;
import org.ambrosia.notification_service.controller.UnauthorizedAsyncNotificationController;
import org.ambrosia.notification_service.dto.UserInfo;
import org.eclipse.microprofile.reactive.messaging.spi.Connector;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.OidcSecurity;
import io.smallrye.mutiny.helpers.test.AssertSubscriber;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import jakarta.inject.Inject;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

import io.quarkus.test.security.oidc.Claim;

import org.junit.jupiter.api.Test;

import com.ambrosia.comment_service.kafka_events.CommentCreated;
import com.ambrosia.content_service.kafka_events.PostCreated;
import com.ambrosia.profile_service.kafka_events.UserAggregation;
import com.ambrosia.profile_service.kafka_events.UserInformation;
import com.google.protobuf.Timestamp;

@QuarkusTest
@QuarkusTestResource(KafkaConnector.class)
class NotificationServiceTest{
    @Inject
    AsyncNotificationController authNotificationController;

    @Inject
    UnauthorizedAsyncNotificationController unauthNotificationController;

    @Inject
    @Connector("smallrye-in-memory")
    InMemoryConnector inMemoryConnector;

    UserInfo testInfo = new UserInfo(UUID.randomUUID().toString(), "test", null);

    @Test
    void unauthorizedAsyncSubTest() throws IOException{
        var in = inMemoryConnector.source("content-channel");
        var dto = createCommentContentMessage();
        in.send(dto.toByteArray());

        unauthNotificationController.consume(2L)
            .subscribe().withSubscriber(AssertSubscriber.create(5))
            .awaitItems(2)
            .cancel();
    }

    @TestSecurity(user = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa", roles = "user")
    @OidcSecurity(claims = {
        @Claim(key = "sub", value = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
        @Claim(key = "id", value = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
    })
    @Test
    void shouldChangeObservedPost() throws IOException{
        var in = inMemoryConnector.source("content-channel");
        var dto = createPostContentMessage();
        in.send(dto.toByteArray());

        var subscriber = AssertSubscriber.create(5);

        authNotificationController.consume(9_999_999L)
            .subscribe().withSubscriber(subscriber);
        given()
            .param("postId", 5)
        .when()
            .patch("/api/user/notification")
        .then()
            .statusCode(204);
        subscriber.awaitItems(1);
    }

    UserAggregation createCommentContentMessage(){
        var instant = Instant.now();
        return UserAggregation.newBuilder()
            .setUser(UserInformation.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setUsername("TestUsername")
                .build()
            )
            .setComment(CommentCreated.newBuilder()
                .setId(9_999_999L)
                .setContent("TestContent")
                .setCreatedAt(Timestamp.newBuilder()
                    .setSeconds(instant.getEpochSecond())
                    .setNanos(instant.getNano())
                )
                .setPostId(9_999_999L)
                .build()
            )
            .build();
    }

    UserAggregation createPostContentMessage(){
        var instant = Instant.now();
        return UserAggregation.newBuilder()
            .setUser(UserInformation.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setUsername("TestUsername")
                .build()
            )
            .setPost(PostCreated.newBuilder()
                .setId(9_999_999L)
                .setTitle("TestTitle")
                .setPublishedAt(Timestamp.newBuilder()
                    .setSeconds(instant.getEpochSecond())
                    .setNanos(instant.getNano())
                    .build()
                )
                .build()
            )
            .build();
    }
}