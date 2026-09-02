package com.ambrosia.comment_service.comment.controller;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.comment_service.comment.service.ModeratorCommentService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user/comment")
public class ModeratorCommentController {
    private final ModeratorCommentService moderatorCommentService;
    
    @DeleteMapping("/{id}")
    public void deleteComment(
        @PathVariable long id,
        @AuthenticationPrincipal Jwt jwt) {
        moderatorCommentService.deleteComment(UUID.fromString(jwt.getSubject()), id);
    }
}
