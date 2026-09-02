package com.ambrosia.profile_service.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.profile_service.core.UserInfo;
import com.ambrosia.profile_service.user.model.dto.response.PublicUserProfileResponse;
import com.ambrosia.profile_service.user.model.dto.response.UserSearch;
import com.ambrosia.profile_service.user.service.UserProfileService;
import com.ambrosia.profile_service.user.service.UserQueryService;
import com.ambrosia.profile_service.user.service.UserSearchService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/public/profile")
public class PublicProfileController {
    private final UserProfileService userService;
    private final UserSearchService userSearchService;
    private final UserQueryService userQueryService;

    @GetMapping("/{username}")
    public PublicUserProfileResponse getInfo(
        @PathVariable String username,
        @AuthenticationPrincipal Jwt jwt
    ) {
        return jwt != null?
            userQueryService.getPublicProfile(username, UUID.fromString(jwt.getSubject())):
            userQueryService.getPublicProfile(username, null);
    }

    @PostMapping("/info")
    public List<UserInfo> getUserInformation(
        @RequestBody @Size(max = 40) List<UUID> userIds) {
        return userService.getUserInfo(userIds);
    }
    
    @GetMapping
    public List<UserSearch> searchUsers(
        @RequestParam(required = true) @Valid @Size(min = 3, max = 32) String searchString) {
        return userSearchService.search(searchString, 10);
    }
    
}
