package com.ambrosia.community_service.community.domain.policy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.ambrosia.community_service.community.domain.policy.view.CommunityViewPolicy;
import com.ambrosia.community_service.core.domain.policy.entity.CommunityUserContext;
import com.ambrosia.community_service.exception.community.NotEnoughPermissionsException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

public class CommunityViewPolicyTest {
    CommunityViewPolicy communityViewPolicy = new CommunityViewPolicy();

    @Test
    void shouldDenyAnonymousViewOnPrivateCommunity() {
        var context = new CommunityUserContext(
            false, 
            false, 
            false, 
            true
        );
        assertThrows(
                NotEnoughPermissionsException.class,
                () -> communityViewPolicy.evaluate(new Actor(UUID.randomUUID(), Role.ANONYMOUS), context));
    }

    @Test
    void shouldAllowAnonymousViewOnPublicCommunity() {
        var context = new CommunityUserContext(
            false, 
            false, 
            false, 
            false
        );
        assertDoesNotThrow(() -> communityViewPolicy.evaluate(
                new Actor(UUID.randomUUID(), Role.ANONYMOUS), context));
    }

    @Test
    void shouldAllowAdminViewOnPrivateCommunity() {
        var context = new CommunityUserContext(
            true, 
            false, 
            false, 
            true
        );
        assertDoesNotThrow(() -> communityViewPolicy.evaluate(
                new Actor(UUID.randomUUID(), Role.ADMIN), context));
    }

    @Test
    void shouldAllowAdminViewOnPublicCommunity() {
        var context = new CommunityUserContext(
            false, 
            false, 
            false, 
            false
        );
        assertDoesNotThrow(() -> communityViewPolicy.evaluate(
                new Actor(UUID.randomUUID(), Role.ADMIN), context));
    }

    @Test
    void shouldAllowModeratorViewOnPrivateCommunity() {
        var context = new CommunityUserContext(
            true, 
            false, 
            true, 
            true
        );
        assertDoesNotThrow(() -> communityViewPolicy.evaluate(
                new Actor(UUID.randomUUID(), Role.USER), context));
    }

    @Test
    void shouldAllowModeratorViewOnPublicCommunity() {
        var context = new CommunityUserContext(
            false, 
            false, 
            true, 
            false);
        assertDoesNotThrow(() -> communityViewPolicy.evaluate(
                new Actor(UUID.randomUUID(), Role.USER), context));
    }

    @Test
    void shouldAllowUserViewOnPublicCommunityWhenFollowed() {
        var context = new CommunityUserContext(
            false, 
            false, 
            false, 
            false
        );
        assertDoesNotThrow(() -> communityViewPolicy.evaluate(
                new Actor(UUID.randomUUID(), Role.USER), context));
    }

    @Test
    void shouldAllowUserViewOnPublicCommunityWhenNotFollowed() {
        var context = new CommunityUserContext(
            false, 
            false, 
            false, 
            false
        );
        assertDoesNotThrow(() -> communityViewPolicy.evaluate(
                new Actor(UUID.randomUUID(), Role.USER), context));
    }

    @Test
    void shouldAllowUserViewOnPublicCommunityWhenBanned() {
        var context = new CommunityUserContext(
            true, 
            false, 
            false, 
            false
        );
        assertDoesNotThrow(() -> communityViewPolicy.evaluate(
                new Actor(UUID.randomUUID(), Role.USER), context));
    }

    @Test
    void shouldAllowUserViewOnPrivateCommunityWhenFollowedAndNotBanned() {
        var context = new CommunityUserContext(
            true, 
            false, 
            false, 
            true);
        assertDoesNotThrow(() -> communityViewPolicy.evaluate(
                new Actor(UUID.randomUUID(), Role.USER), context));
    }

    @Test
    void shouldDenyUserViewOnPrivateCommunityWhenBannedAndFollowed() {
        var context = new CommunityUserContext(
            true, 
            true, 
            false, 
            true);
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> communityViewPolicy.evaluate(
                    new Actor(UUID.randomUUID(), Role.USER), context));
    }

    @Test
    void shouldDenyUserViewOnPrivateCommunityWhenNotFollowedAndNotBanned() {
        var context = new CommunityUserContext(
            false, 
            false, 
            false, 
            true);
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> communityViewPolicy.evaluate(
                    new Actor(UUID.randomUUID(), Role.USER), context));
    }

    @Test
    void shouldDenyUserViewOnPrivateCommunityWhenBannedAndNotFollowed() {
        var context = new CommunityUserContext(
            false,
            true, 
            false, 
            true);
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> communityViewPolicy.evaluate(
                    new Actor(UUID.randomUUID(), Role.USER), context)
        );
    }
}
