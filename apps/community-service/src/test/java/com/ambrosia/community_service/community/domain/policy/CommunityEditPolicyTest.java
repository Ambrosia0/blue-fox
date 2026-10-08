package com.ambrosia.community_service.community.domain.policy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.ambrosia.community_service.community.domain.policy.entity.CommunityEditUserContext;
import com.ambrosia.community_service.community.domain.policy.manage.CommunityEditPolicy;
import com.ambrosia.community_service.core.AppConfiguration;
import com.ambrosia.community_service.exception.community.NotEnoughPermissionsException;
import com.ambrosia.community_service.exception.community.UserDoesntExistException;
import com.ambrosia.community_service.exception.community.UserIsBannedException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

public class CommunityEditPolicyTest {
    AppConfiguration appConfiguration = mock(AppConfiguration.class);
    CommunityEditPolicy policy = new CommunityEditPolicy();
    
    @Test
    void shouldThrowNotEnoughPermissionsExceptionOnInformationEdit() {
        var context = new CommunityEditUserContext(
                UUID.randomUUID(),
                false,
                false
            );
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> policy.evaluate(new Actor(UUID.randomUUID(), Role.USER), context));
    }

    @Test
    void shouldThrowUserIsBannedExceptionOnInformationEditWhenTargetBanned() {
        var id = UUID.randomUUID();
        var context = new CommunityEditUserContext(
                id,  // isOwner = true
                true,  // isAnyTargetBanned = true
                true); // isAllTargetsExists = true
        assertThrows(
                UserIsBannedException.class,
                () -> policy.evaluate(new Actor(id, Role.USER), context));
    }

    @Test
    void shouldThrowUserDoesntExistExceptionOnInformationEditWhenTargetMissing() {
        var id = UUID.randomUUID();
        var context = new CommunityEditUserContext(
                id,   // isOwner = true
                false,  // isAnyTargetBanned = false
                false); // isAllTargetsExists = false
        assertThrows(
            UserDoesntExistException.class,
            () -> policy.evaluate(new Actor(id, Role.USER), context));
    }

    @Test
    void shouldAllowEditInformationWhenAdmin() {
        assertDoesNotThrow(() -> policy.evaluate(
            new Actor(UUID.randomUUID(), Role.ADMIN),
            new CommunityEditUserContext(UUID.randomUUID(), false, true)));
    }

    @Test
    void shouldAllowEditInformationWhenOwnerAndAllTargetsValid() {
        var id = UUID.randomUUID();
        assertDoesNotThrow(() -> policy.evaluate(
            new Actor(id, Role.USER),
            new CommunityEditUserContext(id, false, true)));
    }
}
