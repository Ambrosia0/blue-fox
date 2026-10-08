package com.ambrosia.community_service.community.domain.policy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.ambrosia.community_service.community.domain.policy.manage.CommunityOwnerEditPolicy;
import com.ambrosia.community_service.exception.community.NotEnoughPermissionsException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

public class CommunityDeletePolicyTest {
    public CommunityOwnerEditPolicy policy = new CommunityOwnerEditPolicy();

    @Test
    void shouldAllowEditOwnerWhenAdmin() {
        assertDoesNotThrow(() -> policy.evaluate(new Actor(UUID.randomUUID(), Role.ADMIN), null));
    }

    @Test
    void shouldThrowNotEnoughPermissionsExceptionEditOwnerWhenUser() {
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> policy.evaluate(new Actor(UUID.randomUUID(), Role.USER), null));
    }
}
