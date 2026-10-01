package com.ambrosia.comment_service.comment.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.comment_service.comment.model.dto.request.CreateComment;
import com.ambrosia.comment_service.comment.model.dto.response.CreateCommentResponse;
import com.ambrosia.comment_service.comment.service.UserCommentLikeService;
import com.ambrosia.comment_service.comment.service.UserCommentService;
import com.ambrosia.comment_service.core.policy.UserActor;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user/comment")
@Validated
public class UserCommentController {
    private final UserCommentService userCommentService;

    private final UserCommentLikeService userCommentLikeService;

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping
    public CreateCommentResponse createComment(
            @RequestBody @Valid CreateComment createComment,
            @AuthenticationPrincipal Jwt jwt){
        var policy = new UserActor(UUID.fromString(jwt.getSubject()));
        return userCommentService.createComment(
            policy,
            createComment
        );
    }

    @PostMapping("/{commentId}/attachment/{attachmentId}")
    public CreateCommentResponse confirmUpload(
            @PathVariable Long commentId,
            @PathVariable String attachmentId,
            @AuthenticationPrincipal Jwt jwt) {
        var policy = new UserActor(UUID.fromString(jwt.getSubject()));
        return userCommentService.confirmAttachmentUpload(
            policy,
            commentId,
            attachmentId
        );
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal Jwt jwt){
        var policy = new UserActor(UUID.fromString(jwt.getSubject()));
        userCommentService.deleteComment(commentId, policy);
    }

    @PostMapping("/{commentId}/like")
    public void likeComment(
            @PathVariable long commentId,
            @AuthenticationPrincipal Jwt jwt) {
        var policy = new UserActor(UUID.fromString(jwt.getSubject()));
        userCommentLikeService.likeComment(commentId, policy);
    }

    @DeleteMapping("/{commentId}/like")
    public void unlikeComment(
            @PathVariable long commentId,
            @AuthenticationPrincipal Jwt jwt) {
        var policy = new UserActor(UUID.fromString(jwt.getSubject()));
        userCommentLikeService.unlikeComment(commentId, policy);
    }
    
}
