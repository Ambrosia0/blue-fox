package com.ambrosia.content_service.post.domain.policy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.ambrosia.content_service.exception.api.NotEnoughPermissionsException;
import com.ambrosia.content_service.post.domain.policy.entity.PostDeletePolicyData;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

public class PostDeletePolicyTest {
    PostDeletePolicy policy = new PostDeletePolicy();

    @Test
    void shouldAllowDeleteWhenActorIsAdmin() {
        var actorId = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var data = new PostDeletePolicyData(false, authorId);
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.ADMIN)
                    .build(), 
                data
            )
        );
    }

    @Test
    void shouldAllowDeleteWhenActorIsAuthor() {
        var actorId = UUID.randomUUID();
        var data = new PostDeletePolicyData(false, actorId);
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
            )
        );
    }

    @Test
    void shouldAllowDeleteWhenActorHasDeletePermission() {
        var actorId = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var data = new PostDeletePolicyData(true, authorId);
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
            )
        );
    }

    @Test
    void shouldThrowWhenActorIsNotAuthorNotAdminAndNoPermission() {
        var actorId = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var data = new PostDeletePolicyData(false, authorId);
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
            )
        );
    }

    @Test
    void shouldThrowWhenAnonymousTriesToDeleteNotOwnPost() {
        var authorId = UUID.randomUUID();
        var data = new PostDeletePolicyData(false, authorId);
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> policy.evaluate(
                Actor.builder()
                    .role(Role.ANONYMOUS)
                    .build(), 
                data
            )
        );
    }

    @Test
    void shouldAllowAdminToDeleteEvenWithoutOwnPostOrPermissionFlag() {
        var actorId = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var data = new PostDeletePolicyData(false, authorId);
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.ADMIN)
                    .build(), 
                data
            )
        );
    }
}