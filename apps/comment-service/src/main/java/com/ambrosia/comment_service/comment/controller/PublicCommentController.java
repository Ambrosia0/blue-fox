package com.ambrosia.comment_service.comment.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.comment_service.comment.model.dto.EventFilter;
import com.ambrosia.comment_service.comment.model.dto.response.CommentData;
import com.ambrosia.comment_service.comment.model.dto.response.ScoredCommentData;
import com.ambrosia.comment_service.comment.service.UserCommentService;
import com.ambrosia.comment_service.core.policy.AnonymousActor;
import com.ambrosia.comment_service.core.policy.UserActor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/public")
@Validated
public class PublicCommentController {

    private final UserCommentService userCommentService;

    @GetMapping("/post/{postId}/comments")
    public List<ScoredCommentData> getCommentsForPost(
        @PathVariable long postId,
        @ModelAttribute EventFilter eventFilter,
        @AuthenticationPrincipal Jwt jwt){
        var policy = jwt != null?
            new UserActor(UUID.fromString(jwt.getSubject())):
            new AnonymousActor();
        return userCommentService.getCommentsForPost(postId, eventFilter, policy);
    }

    @GetMapping("/comment/{commentId}")
    public CommentData getComment(
        @PathVariable long commentId,
        @AuthenticationPrincipal Jwt jwt
    ){
        var policy = jwt != null?
            new UserActor(UUID.fromString(jwt.getSubject())):
            new AnonymousActor();
        return userCommentService.getComment(commentId, policy);
    }

    @GetMapping("/comment/{commentId}/tree")
    public List<ScoredCommentData> getCommentTree(
        @PathVariable long commentId,
        @AuthenticationPrincipal Jwt jwt){
        var policy = jwt != null?
            new UserActor(UUID.fromString(jwt.getSubject())):
            new AnonymousActor();
        return userCommentService.getCommentTree(commentId, policy);
    }
}
