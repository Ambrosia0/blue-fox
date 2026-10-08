package com.ambrosia.comment_service.comment.domain.policy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.ambrosia.comment_service.comment.domain.policy.entity.CommentPolicyData;
import com.ambrosia.comment_service.comment.domain.policy.entity.CommunityData;
import com.ambrosia.comment_service.exceptions.api.DoesntFollowedOnPrivateCommunityException;
import com.ambrosia.comment_service.exceptions.api.NotEnoughPermissionsException;
import com.ambrosia.comment_service.exceptions.api.UserBannedException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

public class PostCommentViewPolicyTest {
    PostCommentViewPolicy policy = new PostCommentViewPolicy();

    // ==================== Without community (always allowed) ====================

    @Test
    void shouldAllowViewWhenNoCommunity() {
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
    void shouldAllowAnonymousToViewWhenNoCommunity() {
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
    void shouldAllowViewWhenActorIsAdminOnPublicCommunity() {
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
    void shouldAllowViewOnPublicCommunityWhenUserNotBanned() {
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
    void shouldAllowViewOnPublicCommunityWhenUserIsModerator() {
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

    // ==================== Public community - ANONYMOUS ====================

    @Test
    void shouldAllowAnonymousToViewOnPublicCommunity() {
        var communityData = new CommunityData(false, false, false, false);
        var data = new CommentPolicyData(Optional.of(communityData));
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .role(Role.ANONYMOUS)
                    .build(),
                data
            )
        );
    }

    // ==================== Private community - ADMIN ====================

    @Test
    void shouldAllowViewWhenActorIsAdminOnPrivateCommunity() {
        var actorId = UUID.randomUUID();
        var communityData = new CommunityData(false, false, true, true);
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

    // ==================== Private community - USER is moderator ====================

    @Test
    void shouldAllowViewWhenUserIsModeratorOnPrivateCommunity() {
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

    // ==================== Private community - ANONYMOUS ====================

    @Test
    void shouldThrowNotEnoughPermissionsForAnonymousOnPrivateCommunity() {
        var communityData = new CommunityData(false, false, true, true);
        var data = new CommentPolicyData(Optional.of(communityData));
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

    // ==================== Private community - USER not followed ====================

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

    // ==================== Private community - USER followed but banned ====================

    @Test
    void shouldThrowUserBannedExceptionWhenBannedOnPrivateCommunity() {
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

    // ==================== Private community - USER followed and not banned ====================

    @Test
    void shouldAllowViewWhenFollowedAndNotBannedOnPrivateCommunity() {
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