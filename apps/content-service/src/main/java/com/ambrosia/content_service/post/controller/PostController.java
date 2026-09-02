package com.ambrosia.content_service.post.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ambrosia.content_service.post.model.dto.response.PostContentResponse;
import com.ambrosia.content_service.post.model.dto.response.PreviewWithScoreResponse;
import com.ambrosia.content_service.post.service.user.PostUserService;
import com.ambrosia.content_service.search.model.dto.EventFilter;
import com.ambrosia.content_service.search.model.dto.SearchType;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Validated
@RestController
@RequestMapping("/api/public/post")
public class PostController {
    private final PostUserService postUserService;

    @GetMapping("/{postId}")
    public PostContentResponse getPost(
        @PathVariable long postId,
        @AuthenticationPrincipal Jwt jwt){
        return jwt != null?
            postUserService.getPost(postId, UUID.fromString(jwt.getSubject())):
            postUserService.getPost(postId, null);
    }

    @GetMapping
    public List<PreviewWithScoreResponse> getPostPreviews(
        @ModelAttribute @Valid EventFilter eventFilter,
        @AuthenticationPrincipal Jwt jwt){
        if(eventFilter.searchType() == SearchType.PERSONALIZED && jwt == null)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized!");
        return postUserService.search(eventFilter, jwt != null? UUID.fromString(jwt.getSubject()): null, 10);
    }    
}
