package com.ambrosia.comment_service.comment.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ambrosia.comment_service.BaseIntegrationTest;
import com.ambrosia.comment_service.comment.api.dto.CommentFilter;
import com.ambrosia.comment_service.comment.api.dto.EventFilter;
import com.ambrosia.comment_service.comment.api.dto.EventFilter.SortField;
import com.ambrosia.comment_service.comment.application.query.CommentQueryService;
import com.ambrosia.comment_service.like.domain.repository.LikeRepository;
import com.ambrosia.comment_service.utils.CommentCreator;
import com.ambrosia.comment_service.utils.PostProjectionCreator;
import com.ambrosia.comment_service.utils.UserCreator;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

public class CommentQueryServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired PostProjectionCreator postProjectionCreator;

    @Autowired CommentCreator commentCreator;

    @Autowired CommentQueryService commentQueryService;

    @Autowired UserCreator userCreator;

    @Autowired LikeRepository likeRepository;

    @Test
    void shouldReturnRootCommentsWithoutLike(){
        var post = postProjectionCreator.create();
        var created = List.of(
            commentCreator.create(post.getId()),
            commentCreator.create(post.getId()),
            commentCreator.create(post.getId())
        );
        var resp = commentQueryService.getCommentsForPost(post.getId(), 
            EventFilter.builder()
                .sortField(SortField.HOT)
                .build(), 
            Actor.builder()
                .role(Role.ANONYMOUS)
                .build()
        );
        assertEquals(
            created.size(), 
            resp.stream()
                .filter(c -> c.commentData().isLiked() == false)
                .count()
        );
    }

    @Test
    void shouldReturnRootCommentsWithLike(){
        var post = postProjectionCreator.create();
        commentCreator.create(post.getId());
        commentCreator.create(post.getId());
        var likedComm = commentCreator.create(post.getId());
        var user = userCreator.create();
        likeRepository.add(user.getId(), likedComm.getId());
        var resp = commentQueryService.getCommentsForPost(
            post.getId(), 
            EventFilter.builder().sortField(SortField.HOT).build(), 
            Actor.builder()
                .id(user.getId())
                .role(Role.USER)
                .build()
        );
        assertEquals(
            1, 
            resp.stream()
                .filter(c -> c.commentData().isLiked() != false && 
                    c.commentData().id().equals(likedComm.getId())
                )
                .count()
        );
    }

    @Test
    void shouldReturnTreeCommentsWithoutLike(){
        var post = postProjectionCreator.create();
        var root = commentCreator.create(post.getId());

        var searched = List.of(
            commentCreator.create(post.getId(), root.getId()).getId(),
            commentCreator.create(post.getId(), root.getId()).getId()
        );

        var resp = commentQueryService.getCommentTree(
            root.getId(),
            Actor.builder()
                .role(Role.ANONYMOUS)
                .build()
        );
        assertEquals(
            searched.size(), 
            resp.stream()
                .filter(t -> searched.contains(t.commentData().id()))
                .count()
        );
        assertEquals(0, resp.stream()
            .filter(c -> c.commentData().isLiked() == true)
            .count()
        );
    }

    @Test
    void shouldReturnTreeCommentsWithLike(){
        var post = postProjectionCreator.create();
        var root = commentCreator.create(post.getId());
        commentCreator.create(post.getId(), root.getId());
        commentCreator.create(post.getId(), root.getId());
        var likedComm = commentCreator.create(post.getId(), root.getId());
        var user = userCreator.create();
        likeRepository.add(user.getId(), likedComm.getId());
        var resp = commentQueryService.getCommentTree(
            root.getId(), 
            Actor.builder()
                .id(user.getId())
                .role(Role.USER)
                .build()
        );
        assertEquals(3, resp.size());
        assertEquals(1, resp.stream().filter(c -> c.commentData().isLiked() != null && c.commentData().isLiked()).count());
    }

    @Test 
    void shouldReturnUserComments(){
        var comment = commentCreator.createWithPost();

        assertEquals(
            1,
            commentQueryService.getComments(
                CommentFilter.builder().build(),
                comment.getUserId(),
                Actor.builder()
                    .role(Role.ANONYMOUS)
                    .build(),
                20
            )
                .get()
                .count()
        );
    }
}
