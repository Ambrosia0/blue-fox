package com.ambrosia.comment_service.comment.api;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.comment_service.comment.api.dto.request.CreateComment;
import com.ambrosia.comment_service.comment.api.dto.response.CreateCommentResponse;
import com.ambrosia.comment_service.comment.application.UserCommentLikeService;
import com.ambrosia.comment_service.comment.application.UserCommentService;
import com.ambrosia.library_policy.policy.Actor;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/comment")
@Validated
public class UserCommentController {
    private final UserCommentService userCommentService;

    private final UserCommentLikeService userCommentLikeService;

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping
    public CreateCommentResponse createComment(
            @RequestBody @Valid CreateComment createComment,
            Actor actor
    ){
        return userCommentService.createComment(
            actor,
            createComment
        );
    }

    @PostMapping("/{commentId}/attachment/{attachmentId}")
    public CreateCommentResponse confirmUpload(
            @PathVariable Long commentId,
            @PathVariable String attachmentId,
            Actor actor
    ) {
        return userCommentService.confirmAttachmentUpload(
            actor,
            commentId,
            attachmentId
        );
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(
            @PathVariable Long commentId,
            Actor actor){
        userCommentService.deleteComment(commentId, actor);
    }

    @PostMapping("/{commentId}/like")
    public void likeComment(
            @PathVariable long commentId,
            Actor actor
    ){
        userCommentLikeService.likeComment(commentId, actor);
    }

    @DeleteMapping("/{commentId}/like")
    public void unlikeComment(
            @PathVariable long commentId,
            Actor actor
    ) {
        userCommentLikeService.unlikeComment(commentId, actor);
    }
    
}
