package com.ambrosia.content_service.post.domain.policy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.ambrosia.content_service.exception.api.DoesntFollowedException;
import com.ambrosia.content_service.exception.api.NotEnoughPermissionsException;
import com.ambrosia.content_service.exception.api.UserBannedException;
import com.ambrosia.content_service.post.domain.policy.entity.CommunityData;
import com.ambrosia.content_service.post.domain.policy.entity.PostViewPolicyData;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

public class PostViewPolicyTest {
    PostViewPolicy policy = new PostViewPolicy();

    // ==================== Admin - always allowed ====================

    @Test
    void shouldAllowViewWhenActorIsAdmin() {
        var actorId = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var communityData = new CommunityData(false, false, false, true);
        var data = new PostViewPolicyData(authorId, communityData);
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.ADMIN)
                    .build(), 
                data
            )
        );
    }

    @Test
    void shouldAllowAdminToViewPrivateCommunityPost() {
        var actorId = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var communityData = new CommunityData(false, false, false, true);
        var data = new PostViewPolicyData(authorId, communityData);
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.ADMIN)
                    .build(), 
                data
            )
        );
    }

    @Test
    void shouldAllowAdminToViewBannedUserPost() {
        var actorId = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var communityData = new CommunityData(false, false, true, true);
        var data = new PostViewPolicyData(authorId, communityData);
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.ADMIN)
                    .build(), 
                data
            )
        );
    }

    // ==================== Public community - always allowed ====================

    @Test
    void shouldAllowAnonymousToViewPublicCommunityPost() {
        var authorId = UUID.randomUUID();
        var communityData = new CommunityData(false, false, false, false);
        var data = new PostViewPolicyData(authorId, communityData);
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .role(Role.ANONYMOUS)
                    .build(), 
                data
            )
        );
    }

    @Test
    void shouldAllowUserToViewPublicCommunityPost() {
        var actorId = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var communityData = new CommunityData(false, false, false, false);
        var data = new PostViewPolicyData(authorId, communityData);
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
            )
        );
    }

    @Test
    void shouldAllowUserToViewPublicCommunityPostWhenNotFollowed() {
        var actorId = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var communityData = new CommunityData(false, false, false, false);
        var data = new PostViewPolicyData(authorId, communityData);
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
            )
        );
    }

    @Test
    void shouldAllowUserToViewPublicCommunityPostWhenBannedButNotPrivate() {
        var actorId = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var communityData = new CommunityData(false, false, true, false);
        var data = new PostViewPolicyData(authorId, communityData);
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
            )
        );
    }

    // ==================== Private community - moderator ====================

    @Test
    void shouldAllowViewWhenModeratorInPrivateCommunity() {
        var actorId = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var communityData = new CommunityData(true, false, false, true);
        var data = new PostViewPolicyData(authorId, communityData);
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
            )
        );
    }

    @Test
    void shouldAllowModeratorToViewEvenIfNotFollowed() {
        var actorId = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var communityData = new CommunityData(true, false, false, true);
        var data = new PostViewPolicyData(authorId, communityData);
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
            )
        );
    }

    @Test
    void shouldAllowModeratorToViewEvenIfBanned() {
        var actorId = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var communityData = new CommunityData(true, false, true, true);
        var data = new PostViewPolicyData(authorId, communityData);
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
            )
        );
    }

    // ==================== Private community - ANONYMOUS ====================

    @Test
    void shouldThrowNotEnoughPermissionsForAnonymousOnPrivateCommunity() {
        var authorId = UUID.randomUUID();
        var communityData = new CommunityData(false, false, false, true);
        var data = new PostViewPolicyData(authorId, communityData);
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> policy.evaluate(
                Actor.builder()
                    .role(Role.ANONYMOUS)
                    .build(), 
                data
            )
        );
    }

    // ==================== Private community - USER not followed, not banned ====================

    @Test
    void shouldThrowDoesntFollowedExceptionWhenNotFollowedOnPrivateCommunity() {
        var actorId = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var communityData = new CommunityData(false, false, false, true);
        var data = new PostViewPolicyData(authorId, communityData);
        assertThrows(
            DoesntFollowedException.class,
            () -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
            )
        );
    }

    // ==================== Private community - USER followed but banned ====================

    @Test
    void shouldThrowUserBannedExceptionWhenBannedOnPrivateCommunity() {
        var actorId = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var communityData = new CommunityData(false, true, true, true);
        var data = new PostViewPolicyData(authorId, communityData);
        assertThrows(
            UserBannedException.class,
            () -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
            )
        );
    }

    // ==================== Private community - USER followed and not banned ====================

    @Test
    void shouldAllowViewWhenFollowedAndNotBannedOnPrivateCommunity() {
        var actorId = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var communityData = new CommunityData(false, true, false, true);
        var data = new PostViewPolicyData(authorId, communityData);
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
            )
        );
    }
}