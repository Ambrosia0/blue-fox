package com.ambrosia.community_service.community.domain.policy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.ambrosia.community_service.community.domain.policy.entity.CommunityEditUserContext;
import com.ambrosia.community_service.community.domain.policy.manage.CommunityDeletePolicy;
import com.ambrosia.community_service.exception.community.NotEnoughPermissionsException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

public class CommunityManagePolicyTest {
    CommunityDeletePolicy policy = new CommunityDeletePolicy();

    @Test
    void shouldAllowDeleteWhenOwner() {
        var id = UUID.randomUUID();
        assertDoesNotThrow(() -> policy.evaluate(
            new Actor(id, Role.USER),
            new CommunityEditUserContext(id, false, false))
        );
    }

    @Test
    void shouldAllowDeleteWhenAdmin() {
        assertDoesNotThrow(() -> policy.evaluate(
            new Actor(UUID.randomUUID(), Role.ADMIN),
            new CommunityEditUserContext(UUID.randomUUID(), false, false))
        );
    }

    @Test
    void shouldThrowNotEnoughPermissionsExceptionDeleteWhenNotOwnerNorAdmin() {
        assertThrows(
                NotEnoughPermissionsException.class,
                () -> policy.evaluate(
                    new Actor(UUID.randomUUID(), Role.USER),
                    new CommunityEditUserContext(UUID.randomUUID(), false, false)
                )
        );
    }
}
