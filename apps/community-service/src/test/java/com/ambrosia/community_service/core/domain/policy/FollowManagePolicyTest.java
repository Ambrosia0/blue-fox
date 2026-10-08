package com.ambrosia.community_service.core.domain.policy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.ambrosia.community_service.community.utils.ScopeEnum;
import com.ambrosia.community_service.core.domain.policy.entity.CommunityModeratorUserContext;
import com.ambrosia.community_service.core.domain.policy.moderation.CommunityFollowManagePolicy;
import com.ambrosia.community_service.exception.community.NotEnoughPermissionsException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

public class FollowManagePolicyTest {
    CommunityFollowManagePolicy policy = new CommunityFollowManagePolicy();
    
    @Test
    void shouldThrowNotEnoughPermissionsOnManageFollowWhenNoScope() {
        var context = new CommunityModeratorUserContext(
            Set.of(ScopeEnum.USER_BAN), 
            true, 
            false, 
            false
        );
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> policy.evaluate(
                    new Actor(UUID.randomUUID(), Role.USER), context));
    }

    // @Test
    // void shouldThrowUserDoesntExistOnManageFollowWhenTargetMissing() {
    //     var context = new CommunityModeratorUserContext(
    //         Set.of(ScopeEnum.FOLLOW_MANAGE), 
    //         false, 
    //         false, 
    //         false
    //     );
    //     assertThrows(
    //         UserDoesntExistException.class,
    //         () -> policy.evaluate(
    //                 new Actor(UUID.randomUUID(), Role.USER), context));
    // }

    @Test
    void shouldAllowManageFollowWhenHasScopeAndTargetExists() {
        var context = new CommunityModeratorUserContext(
            Set.of(ScopeEnum.FOLLOW_MANAGE), 
            true, 
            false, 
            false
        );
        assertDoesNotThrow(() -> policy.evaluate(
            new Actor(UUID.randomUUID(), Role.USER), context));
    }
}
