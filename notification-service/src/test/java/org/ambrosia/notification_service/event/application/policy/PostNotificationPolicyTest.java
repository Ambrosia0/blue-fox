package org.ambrosia.notification_service.event.application.policy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.quarkus.test.junit.QuarkusTest;
import org.ambrosia.notification_service.core.policy.Actor;
import org.ambrosia.notification_service.event.domain.policy.PostNotificationPolicyData;
import org.ambrosia.notification_service.exception.UserBannedException;
import org.ambrosia.notification_service.exception.UserDoesntFollowedException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

@QuarkusTest
public class PostNotificationPolicyTest {
    PostNotificationPolicy policy = new PostNotificationPolicy();

    @Test
    void shouldNotThrowExceptionWhenUserIsAdminAndCommunityIsPublic(){
        var actor = Actor.from(UUID.randomUUID(), Actor.Role.ADMIN);
        assertDoesNotThrow(
            () -> policy.validate(
                actor,
                new PostNotificationPolicyData(
                    false,
                    false,
                    false,
                    false
                )
            )
        );
    }
    
    @Test
    void shouldNotThrowExceptionWhenUserIsAdminAndCommunityIsPrivate(){
        var actor = Actor.from(UUID.randomUUID(), Actor.Role.ADMIN);
        assertDoesNotThrow(
            () -> policy.validate(
                actor,
                new PostNotificationPolicyData(
                    false,
                    false,
                    false,
                    true
                )
            )
        );
    }
    
    @Test
    void shouldNotThrowExceptionWhenUserIsAdminAndBanned(){
        var actor = Actor.from(UUID.randomUUID(), Actor.Role.ADMIN);
        assertDoesNotThrow(
            () -> policy.validate(
                actor,
                new PostNotificationPolicyData(
                    false,
                    false,
                    true,
                    true
                )
            )
        );
    }
    
    @Test
    void shouldNotThrowExceptionWhenUserIsAdminAndCommunityIsPrivateAndDoesntFollow(){
        var actor = Actor.from(UUID.randomUUID(), Actor.Role.ADMIN);
        assertDoesNotThrow(
            () -> policy.validate(
                actor,
                new PostNotificationPolicyData(
                    false,
                    true,
                    false,
                    true
                )
            )
        );
    }

    // ==================== MODERATOR scenarios ====================

    @Test
    void shouldNotThrowExceptionWhenUserIsModeratorAndCommunityIsPublic(){
        var actor = Actor.from(UUID.randomUUID(), Actor.Role.USER);
        assertDoesNotThrow(
            () -> policy.validate(
                actor,
                new PostNotificationPolicyData(
                    true,
                    false,
                    false,
                    false
                )
            )
        );
    }

    @Test
    void shouldNotThrowExceptionWhenUserIsModeratorAndCommunityIsPrivate(){
        var actor = Actor.from(UUID.randomUUID(), Actor.Role.USER);
        assertDoesNotThrow(
            () -> policy.validate(
                actor,
                new PostNotificationPolicyData(
                    true,
                    false,
                    false,
                    true
                )
            )
        );
    }

    @Test
    void shouldNotThrowExceptionWhenUserIsModeratorAndBanned(){
        var actor = Actor.from(UUID.randomUUID(), Actor.Role.USER);
        assertDoesNotThrow(
            () -> policy.validate(
                actor,
                new PostNotificationPolicyData(
                    true,
                    false,
                    true,
                    true
                )
            )
        );
    }

    @Test
    void shouldNotThrowExceptionWhenUserIsModeratorAndDoesntFollow(){
        var actor = Actor.from(UUID.randomUUID(), Actor.Role.USER);
        assertDoesNotThrow(
            () -> policy.validate(
                actor,
                new PostNotificationPolicyData(
                    true,
                    false,
                    false,
                    true
                )
            )
        );
    }

    // ==================== USER (non-admin, non-moderator) scenarios ====================

    @Test
    void shouldNotThrowExceptionWhenUserIsRegularUserAndCommunityIsPublic(){
        var actor = Actor.from(UUID.randomUUID(), Actor.Role.USER);
        assertDoesNotThrow(
            () -> policy.validate(
                actor,
                new PostNotificationPolicyData(
                    false,
                    false,
                    false,
                    false
                )
            )
        );
    }

    @Test
    void shouldNotThrowExceptionWhenUserIsRegularUserAndBannedButCommunityIsPublic(){
        var actor = Actor.from(UUID.randomUUID(), Actor.Role.USER);
        assertDoesNotThrow(
            () -> policy.validate(
                actor,
                new PostNotificationPolicyData(
                    false,
                    false,
                    true,
                    false
                )
            )
        );
    }

    @Test
    void shouldNotThrowExceptionWhenUserIsRegularUserAndFollowsPrivateCommunity(){
        var actor = Actor.from(UUID.randomUUID(), Actor.Role.USER);
        assertDoesNotThrow(
            () -> policy.validate(
                actor,
                new PostNotificationPolicyData(
                    false,
                    true,
                    false,
                    true
                )
            )
        );
    }

    @Test
    void shouldThrowUserBannedExceptionWhenUserIsRegularUserAndBannedInPrivateCommunity(){
        var actor = Actor.from(UUID.randomUUID(), Actor.Role.USER);
        assertThrows(
            UserBannedException.class,
            () -> policy.validate(
                actor,
                new PostNotificationPolicyData(
                    false,
                    false,
                    true,
                    true
                )
            )
        );
    }

    @Test
    void shouldThrowUserDoesntFollowedExceptionWhenUserIsRegularUserAndDoesntFollowPrivateCommunity(){
        var actor = Actor.from(UUID.randomUUID(), Actor.Role.USER);
        assertThrows(
            UserDoesntFollowedException.class,
            () -> policy.validate(
                actor,
                new PostNotificationPolicyData(
                    false,
                    false,
                    false,
                    true
                )
            )
        );
    }

    // ==================== ANONYMOUS user scenarios ====================

    @Test
    void shouldNotThrowExceptionWhenUserIsAnonymousAndCommunityIsPublic(){
        var actor = Actor.from(null, Actor.Role.ANONYMOUS);
        assertDoesNotThrow(
            () -> policy.validate(
                actor,
                new PostNotificationPolicyData(
                    false,
                    false,
                    false,
                    false
                )
            )
        );
    }

    @Test
    void shouldThrowUserBannedExceptionWhenUserIsAnonymousAndBannedInPrivateCommunity(){
        var actor = Actor.from(null, Actor.Role.ANONYMOUS);
        assertThrows(
            UserBannedException.class,
            () -> policy.validate(
                actor,
                new PostNotificationPolicyData(
                    false,
                    false,
                    true,
                    true
                )
            )
        );
    }

    @Test
    void shouldThrowUserDoesntFollowedExceptionWhenUserIsAnonymousAndDoesntFollowPrivateCommunity(){
        var actor = Actor.from(null, Actor.Role.ANONYMOUS);
        assertThrows(
            UserDoesntFollowedException.class,
            () -> policy.validate(
                actor,
                new PostNotificationPolicyData(
                    false,
                    false,
                    false,
                    true
                )
            )
        );
    }
}
