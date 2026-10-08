package com.ambrosia.community_service.follow.domain.policy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.ambrosia.community_service.core.domain.policy.entity.CommunityFollowUserContext;
import com.ambrosia.community_service.exception.follow.AlreadyFollowedException;
import com.ambrosia.community_service.exception.follow.AlreadyRequestedFollowException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

public class CommunityFollowPolicyTest {
    CommunityFollowPolicy policy = new CommunityFollowPolicy();

    @Test
    void shouldThrowAlreadyFollowedExceptionWhenAlreadyFollowed() {
        var context = new CommunityFollowUserContext(
            true, 
            false, 
            false,
            false, 
            false
        );
        assertThrows(
            AlreadyFollowedException.class,
            () -> policy.evaluate(new Actor(UUID.randomUUID(), Role.USER), context));
    }

    @Test
    void shouldAllowFollowWhenNotFollowed() {
        var context = new CommunityFollowUserContext(
            false, 
            false, 
            false, 
            false,
            false
        );
        assertDoesNotThrow(() -> policy.evaluate(
                new Actor(UUID.randomUUID(), Role.USER), context));
    }

    @Test
    void shouldAllowFollowWhenNotFollowedOnPrivateCommunity() {
        var context = new CommunityFollowUserContext(
            false, 
            false, 
            false, 
            false,
            true
        );
        assertDoesNotThrow(() -> policy.evaluate(
                new Actor(UUID.randomUUID(), Role.USER), context));
    }

    @Test 
    void shouldThrowAlreadyRequestedFollowExceptionWhenAlreadyRequested(){
        var context = new CommunityFollowUserContext(
            false, 
            false, 
            false,
            true,
            true
        );
        assertThrows(
            AlreadyRequestedFollowException.class,
            () -> policy.evaluate(new Actor(UUID.randomUUID(), Role.USER), context)
        );
    }
}