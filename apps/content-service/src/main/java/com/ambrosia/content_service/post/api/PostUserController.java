package com.ambrosia.content_service.post.api;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.content_service.post.application.PostUserService;
import com.ambrosia.library_policy.policy.Actor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/post")
public class PostUserController {
    private final PostUserService postUserService;
    
    @DeleteMapping("/{id}")
    public void deletePost(
        @PathVariable long id,
        Actor actor) {
        postUserService.deletePost(id, actor);
    }
}
