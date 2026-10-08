package com.ambrosia.content_service.post.domain.policy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.ambrosia.content_service.exception.api.DoesntFollowedException;
import com.ambrosia.content_service.exception.api.PrivateReplyException;
import com.ambrosia.content_service.exception.api.UserBannedException;
import com.ambrosia.content_service.post.domain.policy.entity.CreateCommunityData;
import com.ambrosia.content_service.post.domain.policy.entity.CreatePostData;
import com.ambrosia.content_service.post.domain.policy.entity.PostCreatePolicyData;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

public class PostCreatePolicyTest {
    PostCreatePolicy policy = new PostCreatePolicy();

    // ==================== No community and no reply - always allowed ====================

    @Test
    void shouldAllowCreateWhenNoCommunityAndNoReply() {
        var actorId = UUID.randomUUID();
        var data = new PostCreatePolicyData(Optional.empty(), Optional.empty());
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
    void shouldAllowAnonymousToCreateWithoutCommunity() {
        var data = new PostCreatePolicyData(Optional.empty(), Optional.empty());
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .role(Role.ANONYMOUS)
                    .build(), 
                data
            )
        );
    }

    // ==================== Posting to community - no reply ====================

    @Test
    void shouldAllowCreatePostWhenModeratorInCommunity() {
        var actorId = UUID.randomUUID();
        var community = new CreateCommunityData(1L, true, false, false, true);
        var data = new PostCreatePolicyData(Optional.of(community), Optional.empty());
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
    void shouldAllowCreatePostWhenActorIsAdmin() {
        var actorId = UUID.randomUUID();
        var community = new CreateCommunityData(1L, false, false, false, true);
        var data = new PostCreatePolicyData(Optional.of(community), Optional.empty());
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
    void shouldAllowCreatePostWhenFollowedAndNotBannedInPublicCommunity() {
        var actorId = UUID.randomUUID();
        var community = new CreateCommunityData(1L, false, true, false, false);
        var data = new PostCreatePolicyData(Optional.of(community), Optional.empty());
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
    void shouldAllowCreatePostWhenFollowedAndNotBannedInPrivateCommunity() {
        var actorId = UUID.randomUUID();
        var community = new CreateCommunityData(1L, false, true, false, true);
        var data = new PostCreatePolicyData(Optional.of(community), Optional.empty());
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
    void shouldThrowDoesntFollowedExceptionWhenNotFollowedInPublicCommunity() {
        var actorId = UUID.randomUUID();
        var community = new CreateCommunityData(1L, false, false, false, false);
        var data = new PostCreatePolicyData(Optional.of(community), Optional.empty());
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
        var data = new PostCreatePolicyData(Optional.of(community), Optional.empty());
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
        var data = new PostCreatePolicyData(Optional.of(community), Optional.empty());
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
        var data = new PostCreatePolicyData(Optional.of(community), Optional.empty());
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

    // ==================== Replying to post with community ====================

    @Test
    void shouldAllowReplyWhenNoCommunityAndReplyHasNoCommunity() {
        var actorId = UUID.randomUUID();
        var replyPost = new CreatePostData(Optional.empty());
        var data = new PostCreatePolicyData(Optional.empty(), Optional.of(replyPost));
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
            ));
    }

    @Test
    void shouldAllowReplyWhenReplyingToPostWithModeratorCommunity() {
        var actorId = UUID.randomUUID();
        var replyCommunity = new CreateCommunityData(1L, true, false, false, false);
        var replyPost = new CreatePostData(Optional.of(replyCommunity));
        var data = new PostCreatePolicyData(Optional.empty(), Optional.of(replyPost));
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
            ));
    }

    @Test
    void shouldAllowReplyWhenActorIsAdminReplyingToPost() {
        var actorId = UUID.randomUUID();
        var replyCommunity = new CreateCommunityData(1L, false, false, false, false);
        var replyPost = new CreatePostData(Optional.of(replyCommunity));
        var data = new PostCreatePolicyData(Optional.empty(), Optional.of(replyPost));
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.ADMIN)
                    .build(), 
                data
            ));
    }

    @Test
    void shouldAllowPostAndReplyWhenSameCommunityId() {
        var actorId = UUID.randomUUID();
        var communityId = 1L;
        var postedCommunity = new CreateCommunityData(communityId, false, true, false, true);
        var replyCommunity = new CreateCommunityData(communityId, false, true, false, true);
        var replyPost = new CreatePostData(Optional.of(replyCommunity));
        var data = new PostCreatePolicyData(Optional.of(postedCommunity), Optional.of(replyPost));
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
            ));
    }

    // ==================== PrivateReplyException cases ====================

    @Test
    void shouldThrowPrivateReplyExceptionWhenReplyingFromDifferentPrivateCommunity() {
        var actorId = UUID.randomUUID();
        var postedCommunity = new CreateCommunityData(1L, false, true, false, true);
        var replyCommunity = new CreateCommunityData(2L, false, true, false, true);
        var replyPost = new CreatePostData(Optional.of(replyCommunity));
        var data = new PostCreatePolicyData(Optional.of(postedCommunity), Optional.of(replyPost));
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
        var replyPost = new CreatePostData(Optional.of(replyCommunity));
        var data = new PostCreatePolicyData(Optional.of(postedCommunity), Optional.of(replyPost));
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
        var replyPost = new CreatePostData(Optional.of(replyCommunity));
        var data = new PostCreatePolicyData(Optional.of(postedCommunity), Optional.of(replyPost));
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
        var replyPost = new CreatePostData(Optional.of(replyCommunity));
        var data = new PostCreatePolicyData(Optional.of(postedCommunity), Optional.of(replyPost));
        assertDoesNotThrow(() -> policy.evaluate(
                Actor.builder()
                    .id(actorId)
                    .role(Role.USER)
                    .build(), 
                data
            ));
    }

    // ==================== Banned in reply community ====================

    @Test
    void shouldThrowUserBannedExceptionWhenBannedInReplyCommunity() {
        var actorId = UUID.randomUUID();
        var replyCommunity = new CreateCommunityData(1L, false, true, true, false);
        var replyPost = new CreatePostData(Optional.of(replyCommunity));
        var data = new PostCreatePolicyData(Optional.empty(), Optional.of(replyPost));
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
}