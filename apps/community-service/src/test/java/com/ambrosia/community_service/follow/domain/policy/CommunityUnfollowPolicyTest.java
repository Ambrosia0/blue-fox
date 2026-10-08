package com.ambrosia.community_service.follow.domain.policy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.ambrosia.community_service.core.domain.policy.entity.CommunityUserContext;
import com.ambrosia.community_service.exception.follow.DoesntFollowedException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

public class CommunityUnfollowPolicyTest {
    CommunityUnfollowPolicy policy = new CommunityUnfollowPolicy();

    @Test
    void shouldThrowDoesntFollowedExceptionWhenNotFollowed() {
        var context = new CommunityUserContext(
            false, 
            false, 
            false, 
            false);
        assertThrows(
            DoesntFollowedException.class,
            () -> policy.evaluate(new Actor(UUID.randomUUID(), Role.USER), context));
    }

    @Test
    void shouldAllowUnfollowWhenFollowed() {
        var context = new CommunityUserContext(
            true, 
            false, 
            false, 
            false
        );
        assertDoesNotThrow(() -> policy.evaluate(
                new Actor(UUID.randomUUID(), Role.USER), context));
    }

    @Test
    void shouldAllowUnfollowWhenFollowedOnPrivateCommunity() {
        var context = new CommunityUserContext(
            true, 
            true, 
            false, 
            true
        );
        assertDoesNotThrow(() -> policy.evaluate(
                new Actor(UUID.randomUUID(), Role.USER), context));
    }
}
