package com.ambrosia.content_service.post.domain.policy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.ambrosia.content_service.exception.api.DoesntFollowedException;
import com.ambrosia.content_service.exception.api.NotEnoughPermissionsException;
import com.ambrosia.content_service.exception.api.UserBannedException;
import com.ambrosia.content_service.post.domain.policy.entity.CommunityData;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

public class CommunityViewPolicyTest {
    CommunityViewPolicy policy = new CommunityViewPolicy();

    // ==================== Moderator/Admin cases ====================

    @Test
    void shouldAllowViewWhenActorIsModerator() {
        var actorId = UUID.randomUUID();
        var data = new CommunityData(true, false, false, true);
        assertDoesNotThrow(() -> policy.evaluate(new Actor(actorId, Role.USER), data));
    }

    @Test
    void shouldAllowViewWhenActorIsAdmin() {
        var actorId = UUID.randomUUID();
        var data = new CommunityData(false, false, false, true);
        assertDoesNotThrow(() -> policy.evaluate(new Actor(actorId, Role.ADMIN), data));
    }

    @Test
    void shouldAllowModeratorToViewEvenIfBanned() {
        var actorId = UUID.randomUUID();
        var data = new CommunityData(true, false, true, true);
        assertDoesNotThrow(() -> policy.evaluate(new Actor(actorId, Role.USER), data));
    }

    // ==================== Public community cases ====================

    @Test
    void shouldAllowAnonymousToViewPublicCommunity() {
        var actorId = UUID.randomUUID();
        var data = new CommunityData(false, false, false, false);
        assertDoesNotThrow(() -> policy.evaluate(new Actor(actorId, Role.ANONYMOUS), data));
    }

    @Test
    void shouldAllowUserToViewPublicCommunity() {
        var actorId = UUID.randomUUID();
        var data = new CommunityData(false, false, false, false);
        assertDoesNotThrow(() -> policy.evaluate(new Actor(actorId, Role.USER), data));
    }

    @Test
    void shouldAllowUserToViewPublicCommunityEvenWhenNotFollowed() {
        var actorId = UUID.randomUUID();
        var data = new CommunityData(false, false, false, false);
        assertDoesNotThrow(() -> policy.evaluate(new Actor(actorId, Role.USER), data));
    }

    // ==================== Private community - ANONYMOUS ====================

    @Test
    void shouldThrowNotEnoughPermissionsForAnonymousOnPrivateCommunity() {
        var actorId = UUID.randomUUID();
        var data = new CommunityData(false, false, false, true);
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> policy.evaluate(new Actor(actorId, Role.ANONYMOUS), data)
        );
    }

    // ==================== Private community - USER not followed, not banned ====================

    @Test
    void shouldThrowDoesntFollowedExceptionWhenNotFollowedOnPrivateCommunity() {
        var actorId = UUID.randomUUID();
        var data = new CommunityData(false, false, false, true);
        assertThrows(
            DoesntFollowedException.class,
            () -> policy.evaluate(new Actor(actorId, Role.USER), data)
        );
    }

    // ==================== Private community - USER followed but banned ====================

    @Test
    void shouldThrowUserBannedExceptionWhenBannedOnPrivateCommunity() {
        var actorId = UUID.randomUUID();
        var data = new CommunityData(false, true, true, true);
        assertThrows(
            UserBannedException.class,
            () -> policy.evaluate(new Actor(actorId, Role.USER), data)
        );
    }

    // ==================== Private community - USER followed and not banned ====================

    @Test
    void shouldAllowViewWhenFollowedAndNotBannedOnPrivateCommunity() {
        var actorId = UUID.randomUUID();
        var data = new CommunityData(false, true, false, true);
        assertDoesNotThrow(() -> policy.evaluate(new Actor(actorId, Role.USER), data));
    }

    // ==================== Private community - moderator who is also banned ====================

    @Test
    void shouldAllowModeratorToViewEvenWhenBanned() {
        var actorId = UUID.randomUUID();
        var data = new CommunityData(true, false, true, true);
        assertDoesNotThrow(() -> policy.evaluate(new Actor(actorId, Role.USER), data));
    }
}