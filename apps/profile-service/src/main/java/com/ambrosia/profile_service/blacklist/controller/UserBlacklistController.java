package com.ambrosia.profile_service.blacklist.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.profile_service.blacklist.model.dto.request.BlacklistRequest;
import com.ambrosia.profile_service.blacklist.model.dto.response.BlacklistResponse;
import com.ambrosia.profile_service.blacklist.service.UserBlacklistService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/me/blacklist")
@RestController
public class UserBlacklistController {
    private final UserBlacklistService userBlacklistService;

    @GetMapping
    public Slice<BlacklistResponse> getBlacklist(
        @PageableDefault(page = 0, size = 20) Pageable pageable,
        @AuthenticationPrincipal Jwt jwt) {
        return userBlacklistService.getBlacklistedUsers(
            UUID.fromString(jwt.getSubject()), 
            pageable
        );
    }
    
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{blacklistedUser}")
    public void blacklistUser(
            @PathVariable UUID blacklistedUser,
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid BlacklistRequest request) {
        userBlacklistService.blacklistUser(
            UUID.fromString(jwt.getSubject()),
            blacklistedUser,
            request
        );
    }

    @DeleteMapping("/{userId}")
    public void removeFromBlacklist(
            @PathVariable UUID blacklistedUser,
            @AuthenticationPrincipal Jwt jwt){
        userBlacklistService.removeFromBlacklist(
            UUID.fromString(jwt.getSubject()),
            blacklistedUser
        );
    }
}
