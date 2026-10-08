package com.ambrosia.community_service.core.domain.policy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.ambrosia.community_service.community.utils.ScopeEnum;
import com.ambrosia.community_service.core.domain.policy.entity.CommunityModeratorUserContext;
import com.ambrosia.community_service.core.domain.policy.moderation.CommunityUnbanPolicy;
import com.ambrosia.community_service.exception.community.NotEnoughPermissionsException;
import com.ambrosia.community_service.exception.community.UserDoesntBannedException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

public class UnbanPolicyTest {
    CommunityUnbanPolicy policy = new CommunityUnbanPolicy();

    @Test
    void shouldThrowUserDoesntBannedOnUnbanWhenTargetNotBanned() {
        var context = new CommunityModeratorUserContext(
            Set.of(ScopeEnum.USER_UNBAN), 
            true, 
            false, 
            false
        );
        assertThrows(
            UserDoesntBannedException.class,
            () -> policy.evaluate(new Actor(UUID.randomUUID(), Role.USER), context));
    }

    @Test
    void shouldThrowNotEnoughPermissionsOnUnbanWhenNoScope() {
        var context = new CommunityModeratorUserContext(
            Set.of(ScopeEnum.USER_BAN), 
            true, 
            false, 
            true
        );
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> policy.evaluate(new Actor(UUID.randomUUID(), Role.USER), context));
    }

    @Test
    void shouldAllowUnbanWhenHasScopeAndTargetBanned() {
        var context = new CommunityModeratorUserContext(
            Set.of(ScopeEnum.USER_UNBAN), 
            true, 
            false, 
            true
        );
        assertDoesNotThrow(() -> policy.evaluate(
                new Actor(UUID.randomUUID(), Role.USER), context));
    }
}
