package com.ambrosia.collaboration_service.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.ambrosia.collaboration_service.BaseIntegrationTest;
import com.ambrosia.collaboration_service.grpc.ContentService;
import com.ambrosia.collaboration_service.utils.TestUtils;
import com.ambrosia.content_service.grpc.User;
import com.ambrosia.content_service.kafka_events.collaboration.CollaborationEvent;
import com.ambrosia.content_service.kafka_events.collaboration.CollaborationPostDelete;
import com.ambrosia.content_service.kafka_events.collaboration.CollaborationPostUpdate;

public class CollaborationServiceEventsIntegrationTest extends BaseIntegrationTest{
    @Autowired KafkaTemplate<String, byte[]> kafkaTemplate;
    @Autowired PostCollaborationEditHandler postCollaborationEditHandler;
    @MockitoBean ContentService contentService;

    @Test 
    void shouldCloseSessionOnDeleteEvent() throws Exception{
        var postId = ThreadLocalRandom.current().nextLong();
        var author = TestUtils.createUser();
        var testUser = TestUtils.createUser();

        when(contentService.getCollaborationContent(anyLong()))
            .thenReturn(TestUtils.buildResponse(postId, author, testUser));

        var firstConnection = createSession(UUID.fromString(author.getId()), postId)
            .thenApply(t -> {
                assertTrue(t.isOpen());
                return t;
            })
            .get();

        var secondConnection = createSession(UUID.fromString(testUser.getId()), postId)
            .thenApply(t -> {
                assertTrue(t.isOpen());
                return t;
            })
            .get();

        kafkaTemplate.send(
            "blog.post.collaboration",
            Long.toString(postId),
            createDeleteEvent(postId).toByteArray()
        ).join();

        TestUtils.awaitAssertion(
            () -> assertFalse(() -> (
                firstConnection.isOpen() && 
                secondConnection.isOpen()) &&
                postCollaborationEditHandler.getSessionControllers().containsKey(postId)
            ),
            Duration.ofSeconds(5)
        );
    }

    @Test 
    void shouldCloseConnectionToOldUsersOnCollaborationUsersUpdateThenOpenSessionForNewCollaborator() throws Exception{
        var postId = ThreadLocalRandom.current().nextLong();
        var author = TestUtils.createUser();
        var firstTestUser = TestUtils.createUser();
        var secondTestUser = TestUtils.createUser();
        var thirdTestUser = TestUtils.createUser();

        when(contentService.getCollaborationContent(anyLong()))
            .thenReturn(TestUtils.buildResponse(postId, author, firstTestUser, secondTestUser));

        var firstConnection = createSession(UUID.fromString(firstTestUser.getId()), postId)
            .thenApply(t -> {
                assertTrue(t.isOpen());
                return t;
            })
            .get();

        var secondConnection = createSession(UUID.fromString(secondTestUser.getId()), postId)
            .thenApply(t -> {
                assertTrue(t.isOpen());
                return t;
            })
            .get();

        // session that should fail to connect
        createSession(
            UUID.fromString(thirdTestUser.getId()), 
            postId
        )
            .thenAccept(t -> {
                TestUtils.awaitAssertion(() -> assertFalse(t.isOpen()));
            })
            .join();

        kafkaTemplate.send(
            "blog.post.collaboration",
            Long.toString(postId),
            createUpdateEvent(postId, List.of(thirdTestUser), false).toByteArray()
        ).join();

        TestUtils.awaitAssertion(() -> 
            assertFalse(
                firstConnection.isOpen() &&
                secondConnection.isOpen() &&
                postCollaborationEditHandler.getSessionControllers().containsKey(postId)
            ),
            Duration.ofSeconds(10)
        );

        when(contentService.getCollaborationContent(anyLong()))
            .thenReturn(TestUtils.buildResponse(postId, author, thirdTestUser));

        var repeatedConnection = createSession(
            UUID.fromString(thirdTestUser.getId()), 
            postId
        ).get();

        TestUtils.awaitAssertion(() -> 
            assertTrue(
                repeatedConnection.isOpen() && 
                postCollaborationEditHandler.getSessionControllers().containsKey(postId)
            )
        );
    }

    @Test 
    void shouldCloseConnectionToOldUsersOnCollaborationUsersUpdateThenConnectToOpenSessionWithNewUser() throws Exception{
        var postId = ThreadLocalRandom.current().nextLong();
        var author = TestUtils.createUser();
        var firstTestUser = TestUtils.createUser();
        var secondTestUser = TestUtils.createUser();
        var thirdTestUser = TestUtils.createUser();

        when(contentService.getCollaborationContent(anyLong()))
            .thenReturn(TestUtils.buildResponse(postId, author, firstTestUser, secondTestUser));

        createSession(UUID.fromString(author.getId()), postId)
            .thenApply(t -> {
                assertTrue(t.isOpen());
                return t;
            })
            .get();

        var firstConnection = createSession(UUID.fromString(firstTestUser.getId()), postId)
            .thenApply(t -> {
                assertTrue(t.isOpen());
                return t;
            })
            .get();

        var secondConnection = createSession(UUID.fromString(secondTestUser.getId()), postId)
            .thenApply(t -> {
                assertTrue(t.isOpen());
                return t;
            })
            .get();

        // session that should fail to connect
        createSession(
            UUID.fromString(thirdTestUser.getId()), 
            postId
        )
            .thenAccept(t -> {
                TestUtils.awaitAssertion(() -> assertFalse(t.isOpen()));
            })
            .join();

        kafkaTemplate.send(
            "blog.post.collaboration",
            Long.toString(postId),
            createUpdateEvent(postId, List.of(thirdTestUser), false).toByteArray()
        ).join();

        TestUtils.awaitAssertion(() -> 
            assertFalse(
                firstConnection.isOpen() &&
                secondConnection.isOpen() &&
                !postCollaborationEditHandler.getSessionControllers().containsKey(postId)
            ),
            Duration.ofSeconds(10)
        );

        var repeatedConnection = createSession(
            UUID.fromString(thirdTestUser.getId()), 
            postId
        ).get();

        TestUtils.awaitAssertion(() -> 
            assertTrue(
                repeatedConnection.isOpen() && 
                postCollaborationEditHandler.getSessionControllers().containsKey(postId)
            )
        );
    }

    @Test 
    void shouldCloseRemovedUserConnectionsAndConnectNewUserAfterPartiallyChangingCollaborators() throws Exception{
        var postId = ThreadLocalRandom.current().nextLong();

        var author = TestUtils.createUser();
        var firstTestUser = TestUtils.createUser();
        var secondTestUser = TestUtils.createUser();
        var thirdTestUser = TestUtils.createUser();

        when(contentService.getCollaborationContent(anyLong()))
            .thenReturn(TestUtils.buildResponse(postId, author, firstTestUser, secondTestUser));

        var firstConnection = createSession(UUID.fromString(firstTestUser.getId()), postId)
            .thenApply(t -> {
                assertTrue(t.isOpen());
                return t;
            })
            .get();

        var secondConnection = createSession(UUID.fromString(secondTestUser.getId()), postId)
            .thenApply(t -> {
                assertTrue(t.isOpen());
                return t;
            })
            .get();

        // session that should fail to connect
        createSession(
            UUID.fromString(thirdTestUser.getId()), 
            postId
        )
            .thenAccept(t -> {
                TestUtils.awaitAssertion(() -> assertFalse(t.isOpen()));
            })
            .join();

        kafkaTemplate.send(
            "blog.post.collaboration",
            Long.toString(postId),
            createUpdateEvent(postId, List.of(secondTestUser, thirdTestUser), false).toByteArray()
        ).join();

        TestUtils.awaitAssertion(() -> 
            assertFalse(
                firstConnection.isOpen() &&
                !postCollaborationEditHandler.getSessionControllers().containsKey(postId)
            ),
            Duration.ofSeconds(10)
        );

        var repeatedConnection = createSession(
            UUID.fromString(thirdTestUser.getId()), 
            postId
        ).get();

        TestUtils.awaitAssertion(() -> 
            assertTrue(
                repeatedConnection.isOpen() && 
                secondConnection.isOpen() && 
                postCollaborationEditHandler.getSessionControllers().containsKey(postId)
            )
        );
    }

    @Test 
    void shouldCloseSessionOnPublication() throws Exception{
        var postId = ThreadLocalRandom.current().nextLong();
        var author = TestUtils.createUser();
        var testUser = TestUtils.createUser();

        when(contentService.getCollaborationContent(anyLong()))
            .thenReturn(TestUtils.buildResponse(postId, author, testUser));

        var firstConnection = createSession(UUID.fromString(author.getId()), postId)
            .thenApply(t -> {
                assertTrue(t.isOpen());
                return t;
            })
            .get();

        var secondConnection = createSession(UUID.fromString(testUser.getId()), postId)
            .thenApply(t -> {
                assertTrue(t.isOpen());
                return t;
            })
            .get();

        kafkaTemplate.send(
            "blog.post.collaboration",
            Long.toString(postId),
            createUpdateEvent(postId, List.of(), true).toByteArray()
        ).join();

        TestUtils.awaitAssertion(
            () -> assertFalse(() -> (
                firstConnection.isOpen() && 
                secondConnection.isOpen()) &&
                postCollaborationEditHandler.getSessionControllers().containsKey(postId)
            ),
            Duration.ofSeconds(5)
        );
    }

    private CollaborationEvent createDeleteEvent(Long postId){
        return CollaborationEvent.newBuilder()
            .setPostId(postId)
            .setDelete(CollaborationPostDelete.newBuilder().build())
            .build();
    }

    private CollaborationEvent createUpdateEvent(Long postId, List<User> collaborationUsers, boolean isPublished){
        return CollaborationEvent.newBuilder()
            .setPostId(postId)
            .setUpdate(CollaborationPostUpdate.newBuilder()
                .addAllCollaborationUsers(collaborationUsers)
                .setIsPublished(isPublished)
                .build()
            )
            .build();
    }
}
