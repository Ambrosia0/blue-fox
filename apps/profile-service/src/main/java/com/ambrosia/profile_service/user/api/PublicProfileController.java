package com.ambrosia.profile_service.user.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.profile_service.user.api.dto.response.PublicUserProfileResponse;
import com.ambrosia.profile_service.user.api.dto.response.UserSearch;
import com.ambrosia.profile_service.user.application.query.UserQueryService;
import com.ambrosia.profile_service.user.application.query.UserSearchService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/public/profile")
public class PublicProfileController {
    private final UserSearchService userSearchService;

    private final UserQueryService userQueryService;

    @GetMapping("/{username}")
    public PublicUserProfileResponse getInfo(
        @PathVariable String username,
        Actor actor
    ) {
        return userQueryService.getPublicProfile(username, actor);
    }

    @GetMapping
    public List<UserSearch> searchUsers(
        @RequestParam(required = true) @Valid @Size(min = 3, max = 32) String searchString) {
        return userSearchService.search(searchString, 10);
    }
    
}
