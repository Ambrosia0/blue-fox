package com.ambrosia.comment_service.comment.controller;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.comment_service.comment.model.dto.EventFilter;
import com.ambrosia.comment_service.comment.model.dto.EventFilter.SortField;
import com.ambrosia.comment_service.comment.model.dto.response.RootCommentData;
import com.ambrosia.comment_service.comment.model.dto.response.TreeCommentData;
import com.ambrosia.comment_service.comment.service.CommentQueryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/public")
@Validated
public class PublicCommentController {

    private final CommentQueryService commentQueryService;

    @GetMapping("/post/{postId}/comments")
    public List<RootCommentData> getCommentsForPost(
        @PathVariable long postId,
        @RequestParam(defaultValue = "HOT") SortField sortField,
        @RequestParam(required = false) Long lastSeenId,
        @RequestParam(required = false) Instant lastSeenInstant,
        @RequestParam(required = false) Integer lastSeenCount,
        @RequestParam(required = false) Direction direction,
        @AuthenticationPrincipal Jwt jwt){
        
        var filter = EventFilter.builder()
            .sortField(sortField)
            .lastSeenId(lastSeenId)
            .lastSeenInstant(lastSeenInstant)
            .lastSeenCount(lastSeenCount)
            .direction(direction)
            .build();
        return jwt != null?
            commentQueryService.getCommentsForPost(postId, filter, UUID.fromString(jwt.getSubject())):
            commentQueryService.getCommentsForPost(postId, filter, null);
    }

    @GetMapping("/comment/{commentId}")
    public TreeCommentData getComment(
        @PathVariable long commentId,
        @AuthenticationPrincipal Jwt jwt
    ){
        return jwt != null?
            commentQueryService.getComment(commentId, UUID.fromString(jwt.getSubject())):
            commentQueryService.getComment(commentId, null);
    }

    @GetMapping("/comment/{commentId}/tree")
    public List<TreeCommentData> getCommentTree(
        @PathVariable long commentId,
        @AuthenticationPrincipal Jwt jwt){
        return jwt != null?
            commentQueryService.getCommentTree(commentId, UUID.fromString(jwt.getSubject())):
            commentQueryService.getCommentTree(commentId, null);
    }
}
