package com.ambrosia.comment_service.integration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.comment_service.BaseIntegrationTest;
import com.ambrosia.comment_service.community.service.CommentPermissionService;
import com.ambrosia.comment_service.core.policy.AnonymousActor;
import com.ambrosia.comment_service.core.policy.UserActor;
import com.ambrosia.comment_service.exceptions.api.DoesntFollowedOnPrivateCommunityException;
import com.ambrosia.comment_service.exceptions.api.UserBannedException;
import com.ambrosia.comment_service.utils.CommentCreator;
import com.ambrosia.comment_service.utils.CommunityBanCreator;
import com.ambrosia.comment_service.utils.CommunityCreator;
import com.ambrosia.comment_service.utils.CommunityFollowCreator;
import com.ambrosia.comment_service.utils.PostProjectionCreator;
import com.ambrosia.comment_service.utils.UserCreator;

@Transactional
public class CommunityPermissionServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired CommentPermissionService communityPermissionService;

    @Autowired CommentCreator commentCreator;
    @Autowired CommunityCreator communityCreator;
    @Autowired PostProjectionCreator postProjectionCreator;
    @Autowired CommunityBanCreator communityBanCreator;
    @Autowired CommunityFollowCreator communityFollowCreator;
    @Autowired UserCreator userCreator;

    // ------------------- create cases
    @Test
    void shouldThrowUserBannedExceptionOnCreatePermissionValidation(){
        var community = communityCreator.create(true);
        var post = postProjectionCreator.create(community.getId());
        var user = userCreator.create();
        communityBanCreator.create(community.getId(), user.getId());
        assertThrows(
            UserBannedException.class,
            () -> communityPermissionService.validateCommentCreate(
                new UserActor(user.getId()), 
                post.getId()
            )
        );
    }

    @Test
    void shouldValidatePermissionsOnCommentCreateWithoutCommunity(){
        var post = postProjectionCreator.create();
        var user = userCreator.create();
        assertDoesNotThrow(
            () -> communityPermissionService.validateCommentCreate(
                new UserActor(user.getId()), 
                post.getId()
            )
        );
    }

    @Test
    void shouldThrowDoesntFollowedOnPrivateCommunityExceptionOnCommentCreateValidation(){
        var community = communityCreator.create(true);

        var post = postProjectionCreator.create(community.getId());
        var user = userCreator.create();
        assertThrows(
            DoesntFollowedOnPrivateCommunityException.class,
            () -> communityPermissionService.validateCommentCreate(
                new UserActor(user.getId()), 
                post.getId()
            )
        );
    }

    // ------------------- view cases
    @Test
    void shouldValidatePermissionOnCommentViewWithoutCommunity(){
        var post = postProjectionCreator.create();
        var user = userCreator.create();
        assertDoesNotThrow(
            () -> communityPermissionService.validateCommentView(
                new UserActor(user.getId()), 
                post.getId()
            )
        );
    }

    @Test
    void shouldValidatePermissionsOnCommentViewWithPublicCommunityWithBan(){
        var community = communityCreator.create(false);
        var post = postProjectionCreator.create(community.getId());
        var user = userCreator.create();
        communityBanCreator.create(community.getId(), user.getId());
        assertDoesNotThrow(
            () -> communityPermissionService.validateCommentView(
                new UserActor(user.getId()), post.getId()
            )
        );
    }

    @Test
    void shouldThrowDoesntFollowedOnPrivateCommunityExceptionOnCommentViewWithoutBanAndFollow(){
        var community = communityCreator.create(true);
        var post = postProjectionCreator.create(community.getId());
        var user = userCreator.create();
        assertThrows(
            DoesntFollowedOnPrivateCommunityException.class,
            () -> communityPermissionService.validateCommentView(
                new UserActor(user.getId()), post.getId()
            )
        );
    }

    @Test
    void shouldThrowDoesntFollowedOnPrivateCommunityExceptionOnCommentViewWithBanAndFollow(){
        var community = communityCreator.create(true);
        var post = postProjectionCreator.create(community.getId());
        var user = userCreator.create();
        communityBanCreator.create(community.getId(), user.getId());
        assertThrows(
            DoesntFollowedOnPrivateCommunityException.class,
            () -> communityPermissionService.validateCommentView(
                new UserActor(user.getId()), post.getId()
            )
        );
    }

    @Test
    void shouldValidatePermissionsOnCommentViewForPrivateCommunityAndFollow(){
        var community = communityCreator.create(true);
        var post = postProjectionCreator.create(community.getId());
        var user = userCreator.create();
        communityFollowCreator.create(community.getId(), user.getId());
        assertDoesNotThrow(
            () -> communityPermissionService.validateCommentView(
                new UserActor(user.getId()), post.getId()
            )
        );
    }

    @Test
    void shouldThrowDoesntFollowedOnPrivateCommunityExceptionOnCommentViewUnauth(){
        var community = communityCreator.create(true);
        var post = postProjectionCreator.create(community.getId());
        assertThrows(
            DoesntFollowedOnPrivateCommunityException.class,
            () -> communityPermissionService.validateCommentView(new AnonymousActor(), post.getId())
        );
    }

    @Test
    void shouldValidatePermissionsOnCommentViewUnauthForPublicCommunity(){
        var community = communityCreator.create(false);
        var post = postProjectionCreator.create(community.getId());
        assertDoesNotThrow(
            () -> communityPermissionService.validateCommentView(
                new AnonymousActor(), 
                post.getId()
            )
        );
    }

    //------------------- like/tree cases
    @Test
    void shouldValidatePermissionsOnTreeViewWithoutCommunity(){
        var post = postProjectionCreator.create();
        var user = userCreator.create();
        assertDoesNotThrow(
            () -> communityPermissionService.validateCommentView(
                new UserActor(user.getId()), 
                post.getId()
            )
        );
    }

    @Test
    void shouldThrowDoesntFollowedOnPrivateCommunityExceptionOnTreeViewWithPrivateCommunityUnauth(){
        var community = communityCreator.create(true);
        var post = postProjectionCreator.create(community.getId());
        assertThrows(
            DoesntFollowedOnPrivateCommunityException.class,
            () -> communityPermissionService.validateCommentView(
                new AnonymousActor(), 
                post.getId()
            )
        );
    }

    @Test
    void shouldThrowDoesntFollowedOnPrivateCommunityExceptionOnTreeViewWithPrivateCommunityBanned(){
        var community = communityCreator.create(true);
        var post = postProjectionCreator.create(community.getId());
        var user = userCreator.create();
        communityFollowCreator.create(community.getId(), user.getId());
        communityBanCreator.create(community.getId(), user.getId());
        assertThrows(
            DoesntFollowedOnPrivateCommunityException.class,
            () -> communityPermissionService.validateCommentView(
                new UserActor(user.getId()), 
                post.getId()
            )
        );
    }

    @Test
    void shouldThrowDoesntFollowedOnPrivateCommunityExceptionOnTreeViewWithPrivateCommunityUnfollowed(){
        var community = communityCreator.create(true);
        var post = postProjectionCreator.create(community.getId());
        var user = userCreator.create();
        assertThrows(
            DoesntFollowedOnPrivateCommunityException.class,
            () -> communityPermissionService.validateCommentView(
                new UserActor(user.getId()), 
                post.getId()
            )
        );
    }
}
