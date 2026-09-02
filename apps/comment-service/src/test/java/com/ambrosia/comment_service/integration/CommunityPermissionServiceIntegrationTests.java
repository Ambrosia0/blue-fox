package com.ambrosia.comment_service.integration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.comment_service.BaseIntegrationTest;
import com.ambrosia.comment_service.comment.repository.CommentRepository;
import com.ambrosia.comment_service.community.repository.CommunityProjectionRepository;
import com.ambrosia.comment_service.community.service.CommunityPermissionService;
import com.ambrosia.comment_service.exceptions.api.DoesntFollowedOnPrivateCommunityException;
import com.ambrosia.comment_service.exceptions.api.UserBannedException;
import com.ambrosia.comment_service.grpc.CommunityService;
import com.ambrosia.comment_service.post.repository.PostProjectionRepository;
import com.ambrosia.comment_service.utils.CommentCreator;
import com.ambrosia.comment_service.utils.CommunityBanCreator;
import com.ambrosia.comment_service.utils.CommunityCreator;
import com.ambrosia.comment_service.utils.CommunityFollowCreator;
import com.ambrosia.comment_service.utils.PostProjectionCreator;

@Transactional
public class CommunityPermissionServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired CommunityProjectionRepository communityProjectionRepository;
    @Autowired PostProjectionRepository postProjectionRepository;
    @Autowired CommunityPermissionService communityPermissionService;
    @Autowired CommentRepository commentRepository;

    @MockitoBean CommunityService communityService;

    @Autowired CommentCreator commentCreator;
    @Autowired CommunityCreator communityCreator;
    @Autowired PostProjectionCreator postProjectionCreator;
    @Autowired CommunityBanCreator communityBanCreator;
    @Autowired CommunityFollowCreator communityFollowCreator;

    // ------------------- create cases
    @Test
    void shouldThrowUserBannedExceptionOnCreatePermissionValidation(){
        var community = communityCreator.createFromScratch(true);
        var post = postProjectionCreator.createFromScratch(community.getId());
        var userId = UUID.randomUUID();
        communityBanCreator.create(community.getId(), userId);
        assertThrows(
            UserBannedException.class,
            () -> communityPermissionService.validateCommentCreate(userId, post.getId())
        );
    }

    @Test
    void shouldValidatePermissionsOnCommentCreateWithoutCommunity(){
        var post = postProjectionCreator.createFromScratch();
        var userId = UUID.randomUUID();
        assertDoesNotThrow(() -> communityPermissionService.validateCommentCreate(userId, post.getId()));
    }

    @Test
    void shouldThrowDoesntFollowedOnPrivateCommunityExceptionOnCommentCreateValidation(){
        var community = communityCreator.createFromScratch(true);
        var post = postProjectionCreator.createFromScratch(community.getId());
        var userId = UUID.randomUUID();
        assertThrows(
            DoesntFollowedOnPrivateCommunityException.class,
            () -> communityPermissionService.validateCommentCreate(userId, post.getId())
        );
    }

    // ------------------- view cases
    @Test
    void shouldValidatePermissionOnCommentViewWithoutCommunity(){
        var post = postProjectionCreator.createFromScratch();
        var userId = UUID.randomUUID();
        assertDoesNotThrow(() -> communityPermissionService.validateCommentView(userId, post.getId()));
    }

    @Test
    void shouldValidatePermissionsOnCommentViewWithPublicCommunityWithBan(){
        var community = communityCreator.createFromScratch(false);
        var post = postProjectionCreator.createFromScratch(community.getId());
        var userId = UUID.randomUUID();
        communityBanCreator.create(community.getId(), userId);
        assertDoesNotThrow(() -> communityPermissionService.validateCommentView(userId, post.getId()));
    }

    @Test
    void shouldThrowDoesntFollowedOnPrivateCommunityExceptionOnCommentViewWithoutBanAndFollow(){
        var community = communityCreator.createFromScratch(true);
        var post = postProjectionCreator.createFromScratch(community.getId());
        var userId = UUID.randomUUID();
        assertThrows(
            DoesntFollowedOnPrivateCommunityException.class,
            () -> communityPermissionService.validateCommentView(userId, post.getId())
        );
    }

    @Test
    void shouldThrowDoesntFollowedOnPrivateCommunityExceptionOnCommentViewWithBanAndFollow(){
        var community = communityCreator.createFromScratch(true);
        var post = postProjectionCreator.createFromScratch(community.getId());
        var userId = UUID.randomUUID();
        communityBanCreator.create(community.getId(), userId);
        assertThrows(
            DoesntFollowedOnPrivateCommunityException.class,
            () -> communityPermissionService.validateCommentView(userId, post.getId())
        );
    }

    @Test
    void shouldValidatePermissionsOnCommentViewForPrivateCommunityAndFollow(){
        var community = communityCreator.createFromScratch(true);
        var post = postProjectionCreator.createFromScratch(community.getId());
        var userId = UUID.randomUUID();
        communityFollowCreator.create(community.getId(), userId);
        assertDoesNotThrow(() -> communityPermissionService.validateCommentView(userId, post.getId()));
    }

    @Test
    void shouldThrowDoesntFollowedOnPrivateCommunityExceptionOnCommentViewUnauth(){
        var community = communityCreator.createFromScratch(true);
        var post = postProjectionCreator.createFromScratch(community.getId());
        assertThrows(
            DoesntFollowedOnPrivateCommunityException.class,
            () -> communityPermissionService.validateCommentView(null, post.getId())
        );
    }

    @Test
    void shouldValidatePermissionsOnCommentViewUnauthForPublicCommunity(){
        var community = communityCreator.createFromScratch(false);
        var post = postProjectionCreator.createFromScratch(community.getId());
        assertDoesNotThrow(() -> communityPermissionService.validateCommentView(null, post.getId()));
    }

    //------------------- like/tree cases
    @Test
    void shouldValidatePermissionsOnTreeViewWithoutCommunity(){
        var post = postProjectionCreator.createFromScratch();
        var comment = commentCreator.create(post.getId());
        var userId = UUID.randomUUID();
        assertDoesNotThrow(() -> communityPermissionService.validateCommentTreeView(userId, comment.getId()));
    }

    @Test
    void shouldThrowDoesntFollowedOnPrivateCommunityExceptionOnTreeViewWithPrivateCommunityUnauth(){
        var community = communityCreator.createFromScratch(true);
        var post = postProjectionCreator.createFromScratch(community.getId());
        var comment = commentCreator.create(post.getId());
        assertThrows(
            DoesntFollowedOnPrivateCommunityException.class,
            () -> communityPermissionService.validateCommentTreeView(null, comment.getId())
        );
    }

    @Test
    void shouldThrowDoesntFollowedOnPrivateCommunityExceptionOnTreeViewWithPrivateCommunityBanned(){
        var community = communityCreator.createFromScratch(true);
        var post = postProjectionCreator.createFromScratch(community.getId());
        var comment = commentCreator.create(post.getId());
        var userId = UUID.randomUUID();
        communityBanCreator.create(community.getId(), userId);
        assertThrows(
            DoesntFollowedOnPrivateCommunityException.class,
            () -> communityPermissionService.validateCommentTreeView(userId, comment.getId())
        );
    }

    @Test
    void shouldThrowDoesntFollowedOnPrivateCommunityExceptionOnTreeViewWithPrivateCommunityUnfollowed(){
        var community = communityCreator.createFromScratch(true);
        var post = postProjectionCreator.createFromScratch(community.getId());
        var comment = commentCreator.create(post.getId());
        var userId = UUID.randomUUID();
        assertThrows(
            DoesntFollowedOnPrivateCommunityException.class,
            () -> communityPermissionService.validateCommentTreeView(userId, comment.getId())
        );
    }
}
