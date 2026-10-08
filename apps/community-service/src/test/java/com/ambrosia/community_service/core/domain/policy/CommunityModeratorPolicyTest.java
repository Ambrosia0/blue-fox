package com.ambrosia.community_service.core.domain.policy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.ambrosia.community_service.community.utils.ScopeEnum;
import com.ambrosia.community_service.core.domain.policy.entity.CommunityModeratorUserContext;
import com.ambrosia.community_service.core.domain.policy.moderation.CommunityBanPolicy;
import com.ambrosia.community_service.exception.community.NotEnoughPermissionsException;
import com.ambrosia.community_service.exception.community.UserDoesntExistException;
import com.ambrosia.community_service.exception.community.UserIsModeratorException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

public class CommunityModeratorPolicyTest {
    CommunityBanPolicy policy = new CommunityBanPolicy();

    @Test
    void shouldThrowUserDoesntExistOnBanWhenTargetMissing() {
        var context = new CommunityModeratorUserContext(
            Set.of(ScopeEnum.USER_BAN), 
            false, 
            false, 
            false
        );
        assertThrows(
            UserDoesntExistException.class,
            () -> policy.evaluate(new Actor(UUID.randomUUID(), Role.USER), context));
    }

    @Test
    void shouldThrowUserIsModeratorOnBanWhenTargetIsModerator() {
        var context = new CommunityModeratorUserContext(
            Set.of(ScopeEnum.USER_BAN), 
            true, 
            true, 
            false
        );
        assertThrows(
            UserIsModeratorException.class,
            () -> policy.evaluate(new Actor(UUID.randomUUID(), Role.USER), context));
    }

    @Test
    void shouldThrowNotEnoughPermissionsOnBanWhenNoScope() {
        var context = new CommunityModeratorUserContext(
            Set.of(ScopeEnum.USER_UNBAN), 
            true, 
            false, 
            false
        );
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> policy.evaluate(new Actor(UUID.randomUUID(), Role.USER), context));
    }

    @Test
    void shouldAllowBanWhenHasScope() {
        var context = new CommunityModeratorUserContext(
            Set.of(ScopeEnum.USER_BAN), 
            true, 
            false, 
            false
        );
        assertDoesNotThrow(() -> policy.evaluate(
                new Actor(UUID.randomUUID(), Role.USER), context));
    }
}