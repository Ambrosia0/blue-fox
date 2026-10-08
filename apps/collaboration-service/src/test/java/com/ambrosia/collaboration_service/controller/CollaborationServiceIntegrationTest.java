package com.ambrosia.collaboration_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.socket.BinaryMessage;

import com.ambrosia.collaboration_service.BaseIntegrationTest;
import com.ambrosia.collaboration_service.grpc.ContentService;
import com.ambrosia.collaboration_service.model.entity.message.CollaborationMessage;
import com.ambrosia.collaboration_service.utils.MessageCodes;
import com.ambrosia.collaboration_service.utils.TestUtils;

public class CollaborationServiceIntegrationTest extends BaseIntegrationTest{
    @MockitoBean ContentService contentService;
    @Autowired PostCollaborationEditHandler postCollaborationEditHandler;
    
    @WithMockUser
    @Test 
    void shouldConnectToEndpointAndCreateSessionThenDeleteSession() throws Exception{
        var postId = ThreadLocalRandom.current().nextLong();

        var author = TestUtils.createUser();
        when(contentService.getCollaborationContent(anyLong()))
            .thenReturn(TestUtils.buildResponse(postId, author, TestUtils.createUser()));
        createSession(UUID.fromString(author.getId()), postId)
            .thenAccept(t -> {
                TestUtils.awaitAssertion(
                    () -> assertTrue(
                        postCollaborationEditHandler.getSessionControllers()
                            .containsKey(postId) &&
                        postCollaborationEditHandler.getSessionControllers()
                            .get(postId)
                            .getSession()
                            .hasActiveUser(UUID.fromString(author.getId()))
                    )
                );
                assertTrue(t.isOpen());
                try {
                    t.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            })
            .join();

        TestUtils.awaitAssertion(() -> 
            assertFalse(postCollaborationEditHandler.getSessionControllers()
                .containsKey(postId)
            )
        );
    }

    @WithMockUser
    @Test 
    void shouldCloseConnectionOnInvalidPostId() throws Exception{
        var postId = ThreadLocalRandom.current().nextLong();
        var userId = UUID.randomUUID();
        when(contentService.getCollaborationContent(anyLong()))
            .thenReturn(Optional.empty());
        
        var session = createSession(userId, postId).get();

        TestUtils.awaitAssertion(() -> assertFalse(session.isOpen()));
    }

    @Test 
    void shouldCloseConnectionOnSpecificInvalidUser() throws Exception{
        var postId = ThreadLocalRandom.current().nextLong();

        var author = TestUtils.createUser();
        var firstUser = TestUtils.createUser();
        var secondUser = TestUtils.createUser();

        when(contentService.getCollaborationContent(anyLong()))
            .thenReturn(TestUtils.buildResponse(postId, author, firstUser));
        
        var firstConnection = createSession(UUID.fromString(firstUser.getId()), postId)
            .thenApply(t -> {
                assertTrue(t.isOpen());
                TestUtils.awaitAssertion(() -> assertTrue(postCollaborationEditHandler
                        .getSessionControllers()
                        .containsKey(postId)
                    )
                );
                return t;
            })
            .get();

        createSession(UUID.fromString(secondUser.getId()), postId)
            .thenAccept(c -> {
                TestUtils.awaitAssertion(() -> assertFalse(c.isOpen()));
                TestUtils.awaitAssertion(() -> assertFalse(postCollaborationEditHandler
                    .getSessionControllers()
                    .get(postId)
                    .getSession()
                    .hasActiveUser(UUID.fromString(secondUser.getId()))
                ));
            })
            .join();
            
        firstConnection.close();
    }

    @Test
    void shouldSendRespSync() throws Exception{
        var postId = ThreadLocalRandom.current().nextLong();

        var author = TestUtils.createUser();
        var firstUser = TestUtils.createUser();
        var secondUser = TestUtils.createUser();

        when(contentService.getCollaborationContent(anyLong()))
            .thenReturn(TestUtils.buildResponse(postId, author, firstUser, secondUser));
        
        var firstUserConnection = createSession(UUID.fromString(firstUser.getId()), postId)
            .get();

        var secondUserConnection = createSession(UUID.fromString(secondUser.getId()), postId)
            .thenApply(c -> {
                TestUtils.awaitAssertion(() -> assertTrue(
                    postCollaborationEditHandler
                        .getSessionControllers()
                        .containsKey(postId)
                        &&
                    postCollaborationEditHandler
                        .getSessionControllers()
                        .get(postId)
                        .getSession()
                        .hasActiveUser(UUID.fromString(secondUser.getId()))
                    ));
                try {
                    c.sendMessage(CollaborationMessage
                        .create(MessageCodes.SYNC, new byte[0])
                        .serialize());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                TestUtils.awaitAssertion(() -> assertEquals(1, (int)c.getAttributes().get(MessageCodes.SYNC_RESP.name())));
                return c;
            })
            .get();

        firstUserConnection.close();
        secondUserConnection.close();
    }

    @Test 
    void shouldSendMessage() throws Exception{
        var postId = ThreadLocalRandom.current().nextLong();

        var author = TestUtils.createUser();
        var firstUser = TestUtils.createUser();
        var secondUser = TestUtils.createUser();

        when(contentService.getCollaborationContent(anyLong()))
            .thenReturn(TestUtils.buildResponse(postId, author, firstUser, secondUser));
            
        var firstUserConnection = createSession(UUID.fromString(firstUser.getId()), postId)
            .get();

        createSession(UUID.fromString(secondUser.getId()), postId)
            .thenApply(c -> {
                TestUtils.awaitAssertion(() -> assertTrue(postCollaborationEditHandler
                        .getSessionControllers()
                        .get(postId)
                        .getSession()
                        .hasActiveUser(UUID.fromString(secondUser.getId()))
                    ));
                try {
                    c.sendMessage(CollaborationMessage
                        .create(MessageCodes.MESSAGE, new byte[6])
                        .serialize());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                TestUtils.awaitAssertion(() -> assertEquals(
                    3, (int)firstUserConnection.getAttributes().get("processed")));
                return c;
            })
            .get()
            .close();

        firstUserConnection.close();
    }

    @Test
    void shouldCloseConnectionOnInvalidProtocolOpCode() throws Exception{
        var postId = ThreadLocalRandom.current().nextLong();
        
        var author = TestUtils.createUser();
        var firstUser = TestUtils.createUser();

        when(contentService.getCollaborationContent(anyLong()))
            .thenReturn(TestUtils.buildResponse(postId, author, firstUser));

        var authorConnection = createSession(UUID.fromString(author.getId()), postId)
            .get();
        
        TestUtils.awaitAssertion(
            () -> assertTrue(postCollaborationEditHandler
                    .getSessionControllers()
                    .containsKey(postId)
                )
        );

        var buf = ByteBuffer.allocate(16)
            .put((byte)0xFF);
        var emptyArr = new byte[15];
        buf.put(emptyArr);
        authorConnection.sendMessage(new BinaryMessage(buf.array()));

        TestUtils.awaitAssertion(
            () -> assertFalse(
                authorConnection.isOpen() &&
                postCollaborationEditHandler.getSessionControllers()
                    .containsKey(postId)
            )
        );
    }
}
