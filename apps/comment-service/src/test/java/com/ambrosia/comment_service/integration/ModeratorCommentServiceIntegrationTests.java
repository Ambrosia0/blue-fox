package com.ambrosia.comment_service.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.comment_service.BaseIntegrationTest;
import com.ambrosia.comment_service.comment.model.entity.Comment;
import com.ambrosia.comment_service.comment.repository.CommentRepository;
import com.ambrosia.comment_service.comment.service.ModeratorCommentService;
import com.ambrosia.comment_service.community.repository.CommunityProjectionRepository;
import com.ambrosia.comment_service.exceptions.api.CommentDoesntRelatedException;
import com.ambrosia.comment_service.exceptions.api.NotEnoughPermissionsException;
import com.ambrosia.comment_service.grpc.CommunityService;
import com.ambrosia.comment_service.post.repository.PostProjectionRepository;
import com.ambrosia.comment_service.utils.CommentCreator;
import com.ambrosia.comment_service.utils.CommunityCreator;
import com.ambrosia.comment_service.utils.PostProjectionCreator;

@Transactional
public class ModeratorCommentServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired ModeratorCommentService moderatorCommentService;
    @Autowired PostProjectionRepository postProjectionRepository;
    @Autowired CommunityProjectionRepository communityProjectionRepository;
    @Autowired CommentRepository commentRepository;

    @Autowired PostProjectionCreator postProjectionCreator;

    @Autowired CommentCreator commentCreator;
    @Autowired CommunityCreator communityCreator;

    @MockitoBean CommunityService communityService;


    @Test
    void shouldThrowCommentDoesntRelatedException(){
        var commentId = 999L;
        assertThrows(
            CommentDoesntRelatedException.class,
            () -> moderatorCommentService.deleteComment(UUID.randomUUID(), commentId));
    }

    @Test
    void shouldThrowNotEnoughPermissionsException(){
        when(communityService.isUserAllowed(any(UUID.class), any(String.class), anyLong())).thenReturn(false);
        var comment = createComment();
        var userId = UUID.randomUUID();
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> moderatorCommentService.deleteComment(userId, comment.getId()));
    }

    @Test
    void shouldHideCommentWhenUserHasPermissions(){
        when(communityService.isUserAllowed(any(UUID.class), any(String.class), anyLong())).thenReturn(true);
        var comment = createComment();
        var userId = UUID.randomUUID();
        moderatorCommentService.deleteComment(userId, comment.getId());
        var hiddenComment = commentRepository.findById(comment.getId()).orElse(null);
        assertEquals(false, hiddenComment.isVisible());
    }


    private Comment createComment(){
        var community = communityCreator.createFromScratch(false);
        var post = postProjectionCreator.createFromScratch(community.getId());
        return commentCreator.create(post.getId());
    }
}