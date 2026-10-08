package com.ambrosia.community_service.community.domain.policy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ambrosia.community_service.community.domain.policy.entity.CommunityCreationUserContext;
import com.ambrosia.community_service.community.domain.policy.manage.CommunityCreatePolicy;
import com.ambrosia.community_service.core.AppConfiguration;
import com.ambrosia.community_service.exception.community.ExceededOwnedCommunityLimitException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

public class CommunityCreatePolicyTest {
    AppConfiguration appConfiguration = mock(AppConfiguration.class);
    CommunityCreatePolicy policy;

    @BeforeEach
    void init() {
        policy = new CommunityCreatePolicy(appConfiguration);
        when(appConfiguration.getMaxOwnedCommunitiesPerUser()).thenReturn(3);
    }

    @Test
    void shouldAllowCreateWhenUnderLimit() {
        when(appConfiguration.getMaxOwnedCommunitiesPerUser()).thenReturn(3);
        var context = new CommunityCreationUserContext(2);
        assertDoesNotThrow(() -> policy.evaluate(new Actor(UUID.randomUUID(), Role.USER), context));
    }

    @Test
    void shouldThrowExceededOwnedCommuinityLimitExceptionWhenAtLimit() {
        when(appConfiguration.getMaxOwnedCommunitiesPerUser()).thenReturn(3);
        var context = new CommunityCreationUserContext(3);
        assertThrows(
            ExceededOwnedCommunityLimitException.class,
            () -> policy.evaluate(new Actor(UUID.randomUUID(), Role.USER), context)
        );
    }

    @Test
    void shouldThrowExceededOwnedCommunityLimitExceptionWhenOverLimit() {
        when(appConfiguration.getMaxOwnedCommunitiesPerUser()).thenReturn(3);
        var context = new CommunityCreationUserContext(4);
        assertThrows(
            ExceededOwnedCommunityLimitException.class,
            () -> policy.evaluate(new Actor(UUID.randomUUID(), Role.USER), context));
    }

}
