package com.ambrosia.comment_service.comment.domain.policy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.ambrosia.comment_service.comment.domain.policy.entity.CommentPolicyData;
import com.ambrosia.comment_service.comment.domain.policy.entity.CommunityData;
import com.ambrosia.comment_service.exceptions.api.UserBannedException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

public class PostCommentCreatePolicyTest {
    PostCommentCreatePolicy policy = new PostCommentCreatePolicy();

    // ==================== Without community (always allowed) ====================

    @Test
    void shouldAllowCreateWhenNoCommunity() {
        var actorId = UUID.randomUUID();
        var data = new CommentPolicyData(Optional.empty());
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
    void shouldAllowAnonymousToCreateWhenNoCommunity() {
        var data = new CommentPolicyData(Optional.empty());
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .role(Role.ANONYMOUS)
                    .build(),
                data
            )
        );
    }

    // ==================== Public community - ADMIN ====================

    @Test
    void shouldAllowCreateWhenActorIsAdminOnPublicCommunity() {
        var actorId = UUID.randomUUID();
        var communityData = new CommunityData(false, false, false, false);
        var data = new CommentPolicyData(Optional.of(communityData));
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.ADMIN)
                    .build(),
                data
            )
        );
    }

    // ==================== Public community - USER ====================

    @Test
    void shouldAllowCreateOnPublicCommunityWhenUserNotBanned() {
        var actorId = UUID.randomUUID();
        var communityData = new CommunityData(false, false, false, false);
        var data = new CommentPolicyData(Optional.of(communityData));
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
    void shouldAllowCreateOnPublicCommunityWhenUserIsModerator() {
        var actorId = UUID.randomUUID();
        var communityData = new CommunityData(false, true, false, false);
        var data = new CommentPolicyData(Optional.of(communityData));
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
    void shouldThrowUserBannedExceptionOnPublicCommunityWhenBanned() {
        var actorId = UUID.randomUUID();
        var communityData = new CommunityData(true, false, false, false);
        var data = new CommentPolicyData(Optional.of(communityData));
        assertThrows(
            UserBannedException.class,
            () -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(),
                data
            )
        );
    }

    // ==================== Private community - USER ====================

    @Test
    void shouldThrowUserBannedExceptionOnPrivateCommunityWhenBanned() {
        var actorId = UUID.randomUUID();
        var communityData = new CommunityData(true, false, true, true);
        var data = new CommentPolicyData(Optional.of(communityData));
        assertThrows(
            UserBannedException.class,
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
    void shouldAllowCreateOnPrivateCommunityWhenNotBanned() {
        var actorId = UUID.randomUUID();
        var communityData = new CommunityData(false, false, true, true);
        var data = new CommentPolicyData(Optional.of(communityData));
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(),
                data
            )
        );
    }
}