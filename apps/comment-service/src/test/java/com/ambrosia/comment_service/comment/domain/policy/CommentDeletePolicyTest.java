package com.ambrosia.comment_service.comment.domain.policy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.ambrosia.comment_service.comment.domain.policy.entity.DeletePolicyData;
import com.ambrosia.comment_service.exceptions.api.NotEnoughPermissionsException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

public class CommentDeletePolicyTest {
    CommentDeletePolicy policy = new CommentDeletePolicy();

    // ==================== Admin ====================

    @Test
    void shouldAllowDeleteWhenActorIsAdmin() {
        var actorId = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var data = new DeletePolicyData(authorId, false);
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.ADMIN)
                    .build(),
                data
            )
        );
    }

    // ==================== Moderator ====================

    @Test
    void shouldAllowDeleteWhenActorIsModerator() {
        var actorId = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var data = new DeletePolicyData(authorId, true);
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(),
                data
            )
        );
    }

    // ==================== Author ====================

    @Test
    void shouldAllowDeleteWhenActorIsAuthor() {
        var actorId = UUID.randomUUID();
        var data = new DeletePolicyData(actorId, false);
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(),
                data
            )
        );
    }

    // ==================== Regular User (not author, not admin, not moderator) ====================

    @Test
    void shouldThrowNotEnoughPermissionsForRegularUser() {
        var actorId = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var data = new DeletePolicyData(authorId, false);
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
    void shouldThrowNotEnoughPermissionsForAnonymousUser() {
        var authorId = UUID.randomUUID();
        var data = new DeletePolicyData(authorId, false);
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
}
