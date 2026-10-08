package com.ambrosia.comment_service.comment.domain.policy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.ambrosia.comment_service.comment.domain.policy.entity.CommentPolicyData;
import com.ambrosia.comment_service.comment.domain.policy.entity.CommunityData;
import com.ambrosia.comment_service.exceptions.api.DoesntFollowedOnPrivateCommunityException;
import com.ambrosia.comment_service.exceptions.api.UserBannedException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

public class CommentLikePolicyTest {
    CommentLikePolicy policy = new CommentLikePolicy();

    // ==================== Without community (always allowed) ====================

    @Test
    void shouldAllowLikeWhenNoCommunity() {
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
    void shouldAllowAnonymousToLikeWhenNoCommunity() {
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
    void shouldAllowLikeWhenActorIsAdminOnPublicCommunity() {
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
    void shouldAllowLikeOnPublicCommunityWhenUserNotBanned() {
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
    void shouldAllowLikeOnPublicCommunityWhenUserIsModerator() {
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
    void shouldThrowDoesntFollowedOnPrivateCommunityExceptionWhenNotFollowed() {
        var actorId = UUID.randomUUID();
        var communityData = new CommunityData(false, false, true, false);
        var data = new CommentPolicyData(Optional.of(communityData));
        assertThrows(
            DoesntFollowedOnPrivateCommunityException.class,
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
    void shouldAllowLikeOnPrivateCommunityWhenFollowedAndNotBanned() {
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

    @Test
    void shouldAllowLikeOnPrivateCommunityWhenIsModerator() {
        var actorId = UUID.randomUUID();
        var communityData = new CommunityData(false, true, true, false);
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