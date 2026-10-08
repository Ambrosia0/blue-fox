package com.ambrosia.content_service.post.domain.policy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.ambrosia.content_service.exception.api.DoesntFollowedException;
import com.ambrosia.content_service.exception.api.NotEnoughPermissionsException;
import com.ambrosia.content_service.exception.api.PrivateReplyException;
import com.ambrosia.content_service.exception.api.UserBannedException;
import com.ambrosia.content_service.post.domain.policy.entity.CreateCommunityData;
import com.ambrosia.content_service.post.domain.policy.entity.PostPublishPolicyData;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

public class PostPublishPolicyTest {
    PostPublishPolicy policy = new PostPublishPolicy();

    // ==================== Author permission check ====================

    @Test
    void shouldThrowWhenActorIsNotAuthor() {
        var actorId = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var data = new PostPublishPolicyData(authorId, Optional.empty(), Optional.empty());
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
            )
        );
    }

    @Test
    void shouldAllowWhenActorIsAuthorAndNoCommunity() {
        var actorId = UUID.randomUUID();
        var data = new PostPublishPolicyData(actorId, Optional.empty(), Optional.empty());
        assertDoesNotThrow(
            () -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
            )
        );
    }

    // ==================== Posting to community without reply ====================

    @Test
    void shouldAllowPublishWhenModeratorInCommunity() {
        var actorId = UUID.randomUUID();
        var community = new CreateCommunityData(1L, true, false, false, true);
        var data = new PostPublishPolicyData(actorId, Optional.of(community), Optional.empty());
        assertDoesNotThrow(
            () -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
            )
        );
    }

    @Test
    void shouldAllowPublishWhenActorIsAdmin() {
        var actorId = UUID.randomUUID();
        var community = new CreateCommunityData(1L, false, false, false, true);
        var data = new PostPublishPolicyData(actorId, Optional.of(community), Optional.empty());
        assertDoesNotThrow(
            () -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.ADMIN)
                    .build(), 
                data
            )
        );
    }

    @Test
    void shouldAllowPublishWhenFollowedAndNotBannedInPublicCommunity() {
        var actorId = UUID.randomUUID();
        var community = new CreateCommunityData(1L, false, true, false, false);
        var data = new PostPublishPolicyData(actorId, Optional.of(community), Optional.empty());
        assertDoesNotThrow(
            () -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
            )
        );
    }

    @Test
    void shouldAllowPublishWhenFollowedAndNotBannedInPrivateCommunity() {
        var actorId = UUID.randomUUID();
        var community = new CreateCommunityData(1L, false, true, false, true);
        var data = new PostPublishPolicyData(actorId, Optional.of(community), Optional.empty());
        assertDoesNotThrow(
            () -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
            )
        );
    }

    @Test
    void shouldThrowDoesntFollowedExceptionWhenNotFollowedInPublicCommunity() {
        var actorId = UUID.randomUUID();
        var community = new CreateCommunityData(1L, false, false, false, false);
        var data = new PostPublishPolicyData(actorId, Optional.of(community), Optional.empty());
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

    @Test
    void shouldThrowDoesntFollowedExceptionWhenNotFollowedInPrivateCommunity() {
        var actorId = UUID.randomUUID();
        var community = new CreateCommunityData(1L, false, false, false, true);
        var data = new PostPublishPolicyData(actorId, Optional.of(community), Optional.empty());
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

    @Test
    void shouldThrowUserBannedExceptionWhenBannedInCommunity() {
        var actorId = UUID.randomUUID();
        var community = new CreateCommunityData(1L, false, true, true, false);
        var data = new PostPublishPolicyData(actorId, Optional.of(community), Optional.empty());
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

    @Test
    void shouldThrowUserBannedExceptionWhenBannedInPrivateCommunity() {
        var actorId = UUID.randomUUID();
        var community = new CreateCommunityData(1L, false, true, true, true);
        var data = new PostPublishPolicyData(actorId, Optional.of(community), Optional.empty());
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

    // ==================== Publishing with reply ====================

    @Test
    void shouldAllowPublishWithReplyWhenSameCommunityId() {
        var actorId = UUID.randomUUID();
        var communityId = 1L;
        var postedCommunity = new CreateCommunityData(communityId, false, true, false, true);
        var replyCommunity = new CreateCommunityData(communityId, false, true, false, true);
        var data = new PostPublishPolicyData(actorId, Optional.of(postedCommunity), Optional.of(replyCommunity));
        assertDoesNotThrow(
            () -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
            )
        );
    }

    // ==================== PrivateReplyException cases ====================

    @Test
    void shouldThrowPrivateReplyExceptionWhenReplyingFromDifferentPrivateCommunity() {
        var actorId = UUID.randomUUID();
        var postedCommunity = new CreateCommunityData(1L, false, true, false, true);
        var replyCommunity = new CreateCommunityData(2L, false, true, false, true);
        var data = new PostPublishPolicyData(actorId, Optional.of(postedCommunity), Optional.of(replyCommunity));
        assertThrows(
            PrivateReplyException.class,
            () -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
            )
        );
    }

    @Test
    void shouldThrowPrivateReplyExceptionWhenPostedCommunityPrivateAndReplyCommunityDifferent() {
        var actorId = UUID.randomUUID();
        var postedCommunity = new CreateCommunityData(1L, false, true, false, true);
        var replyCommunity = new CreateCommunityData(2L, false, true, false, false);
        var data = new PostPublishPolicyData(actorId, Optional.of(postedCommunity), Optional.of(replyCommunity));
        assertThrows(
            PrivateReplyException.class,
            () -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
                )
        );
    }

    @Test
    void shouldThrowPrivateReplyExceptionWhenReplyCommunityPrivateAndPostedCommunityDifferent() {
        var actorId = UUID.randomUUID();
        var postedCommunity = new CreateCommunityData(1L, false, true, false, false);
        var replyCommunity = new CreateCommunityData(2L, false, true, false, true);
        var data = new PostPublishPolicyData(actorId, Optional.of(postedCommunity), Optional.of(replyCommunity));
        assertThrows(
            PrivateReplyException.class,
            () -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(),
                    data
                )
        );
    }

    @Test
    void shouldNotThrowPrivateReplyExceptionWhenSameCommunityOnePrivate() {
        var actorId = UUID.randomUUID();
        var communityId = 1L;
        var postedCommunity = new CreateCommunityData(communityId, false, true, false, true);
        var replyCommunity = new CreateCommunityData(communityId, false, true, false, false);
        var data = new PostPublishPolicyData(actorId, Optional.of(postedCommunity), Optional.of(replyCommunity));
        assertDoesNotThrow(
            () -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(),
                data
            )
        );
    }

    // ==================== validateReplyToPost (banned check only) ====================

    @Test
    void shouldAllowPublishWhenBannedInReplyCommunityButNotModerator() {
        var actorId = UUID.randomUUID();
        var postedCommunity = new CreateCommunityData(1L, false, true, false, false);
        var replyCommunity = new CreateCommunityData(1L, false, true, true, false);
        var data = new PostPublishPolicyData(actorId, Optional.of(postedCommunity), Optional.of(replyCommunity));
        // Only banned check for reply, no followed check - but posted is not banned so should pass
        assertDoesNotThrow(
            () -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
            )
        );
    }
}