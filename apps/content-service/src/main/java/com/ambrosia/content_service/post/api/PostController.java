package com.ambrosia.content_service.post.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ambrosia.content_service.post.api.dto.EventFilter;
import com.ambrosia.content_service.post.api.dto.EventFilter.SearchType;
import com.ambrosia.content_service.post.api.dto.response.PostContentResponse;
import com.ambrosia.content_service.post.api.dto.response.PreviewWithScoreResponse;
import com.ambrosia.content_service.post.application.PostUserService;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/public/post")
public class PostController {
    private final PostUserService postUserService;

    @GetMapping("/{postId}")
    public PostContentResponse getPost(
        @PathVariable long postId,
        Actor actor){
        return postUserService.getPost(postId, actor);
    }

    @GetMapping
    public List<PreviewWithScoreResponse> getPostPreviews(
        @ModelAttribute @Valid EventFilter eventFilter,
        Actor actor){
        if(eventFilter.searchType() == SearchType.PERSONALIZED && actor.role() == Role.ANONYMOUS)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized!");
        return postUserService.search(eventFilter, actor, 10);
    }    
}
