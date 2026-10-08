package com.ambrosia.comment_service.comment.api;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Slice;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.comment_service.comment.api.dto.CommentFilter;
import com.ambrosia.comment_service.comment.api.dto.EventFilter;
import com.ambrosia.comment_service.comment.api.dto.response.CommentData;
import com.ambrosia.comment_service.comment.api.dto.response.ScoredCommentData;
import com.ambrosia.comment_service.comment.application.query.CommentQueryService;
import com.ambrosia.library_policy.policy.Actor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/public")
@Validated
public class PublicCommentController {

    private final CommentQueryService commentQueryService;

    @GetMapping("/post/{postId}/comments")
    public List<ScoredCommentData> getCommentsForPost(
            @PathVariable long postId,
            @ModelAttribute EventFilter eventFilter,
            Actor actor
    ){
        return commentQueryService.getCommentsForPost(postId, eventFilter, actor);
    }

    @GetMapping("/comment/{commentId}")
    public CommentData getComment(
        @PathVariable long commentId,
        Actor actor
    ){
        return commentQueryService.getComment(commentId, actor);
    }

    @GetMapping("/comment/user/{userId}")
    public Slice<CommentData> getComments(
            @ModelAttribute CommentFilter commentFilter,
            @PathVariable UUID userId,
            Actor actor
    ) {
        return commentQueryService.getComments(commentFilter, userId, actor, 10);
    }
    

    @GetMapping("/comment/{commentId}/tree")
    public List<ScoredCommentData> getCommentTree(
            @PathVariable long commentId,
            Actor actor
    ){
        return commentQueryService.getCommentTree(commentId, actor);
    }
}
