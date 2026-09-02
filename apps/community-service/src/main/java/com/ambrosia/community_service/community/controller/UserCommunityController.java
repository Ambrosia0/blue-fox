package com.ambrosia.community_service.community.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.community_service.community.model.dto.request.CommunityCreate;
import com.ambrosia.community_service.community.model.dto.request.CommunityEdit;
import com.ambrosia.community_service.community.model.dto.request.FileMetadata;
import com.ambrosia.community_service.community.model.dto.request.ScopePair;
import com.ambrosia.community_service.community.model.dto.response.AvatarUploadResponse;
import com.ambrosia.community_service.community.model.dto.response.CommunityResponse;
import com.ambrosia.community_service.community.model.dto.response.CommunityScopeResponse;
import com.ambrosia.community_service.community.service.CommunityManageService;
import com.ambrosia.community_service.community.service.CommunityModeratorService;
import com.ambrosia.community_service.community.service.ScopeLinkService;
import com.ambrosia.community_service.community.utils.ScopeEnum;
import com.ambrosia.community_service.community.utils.policy.UserActor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/user/community")
public class UserCommunityController {
    private final CommunityManageService communityManageService;

    private final CommunityModeratorService communityModeratorService;

    private final ScopeLinkService scopeLinkService;
    
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping
    public CommunityResponse createCommunity(
            @RequestBody CommunityCreate createRequest,
            @AuthenticationPrincipal Jwt jwt) {
        return communityManageService.createCommunity(createRequest, UUID.fromString(jwt.getSubject()));
    }

    @PatchMapping("/{id}")
    public CommunityResponse editCommunityInfo(
            @PathVariable long id,
            @RequestBody @Valid CommunityEdit communityEdit,
            @AuthenticationPrincipal Jwt jwt){
        return communityManageService.editCommunityInfo(
            id, 
            communityEdit, 
            new UserActor(UUID.fromString(jwt.getSubject()))
        );
    }

    @PutMapping("/{id}/avatar")
    public AvatarUploadResponse editCommunityAvatar(
            @PathVariable Long id,
            @RequestBody(required = false) FileMetadata fileMetadata,
            @AuthenticationPrincipal Jwt jwt) {
        return communityManageService.uploadAvatar(
            id, 
            fileMetadata,
            new UserActor(UUID.fromString(jwt.getSubject()))
        );
    }

    @PostMapping("/{id}/avatar/{avatarId}")
    public void confirmAvatarUpload(
            @PathVariable String avatarId,
            @PathVariable Long communityId,
            @AuthenticationPrincipal Jwt jwt) {        
        communityManageService.validateAvatarUpload(
            communityId, 
            avatarId,
            new UserActor(UUID.fromString(jwt.getSubject()))
        );
    }
    
    
    @PostMapping("/{id}/ban/{userId}")
    public void banUser(
            @PathVariable Long communityId,
            @PathVariable UUID userId,
            @RequestBody Instant beforeDate,
            @AuthenticationPrincipal Jwt jwt) {
        communityModeratorService.banUser(
            communityId, 
            UUID.fromString(jwt.getSubject()), 
            userId,
            beforeDate
        );
    }

    @DeleteMapping("/{id}/ban/{userId}")
    public void unbanUser(
            @PathVariable Long communityId,
            @PathVariable UUID userId,
            @AuthenticationPrincipal Jwt jwt) {
        communityModeratorService.unbanUser(
            communityId, 
            UUID.fromString(jwt.getSubject()), 
            userId
        );
    }
    
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}/scopes")
    public void editCommunityScopes(
            @PathVariable long id,
            @RequestBody @Valid @Size(max = 3) List<@Valid ScopePair> userScopes,
            @AuthenticationPrincipal Jwt jwt) {
        communityManageService.editCommunityScopes(
            id, 
            userScopes, 
            new UserActor(UUID.fromString(jwt.getSubject()))
        );
    }
    
    @GetMapping("/{id}/me/scopes")
    public CommunityScopeResponse getUserScopes(
            @PathVariable long id,
            @AuthenticationPrincipal Jwt jwt) {
        return scopeLinkService.getUserScopes(id, UUID.fromString(jwt.getSubject()));
    }

    @GetMapping("/{id}/scopes")
    public List<CommunityScopeResponse> getCommunityScopes(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        return communityModeratorService.getUsersScopesForCommunity(
            id, 
            UUID.fromString(jwt.getSubject())
        );
    }
    
    @GetMapping("/scopes")
    public List<ScopeEnum> getScopes() {
        return scopeLinkService.getScopes();
    }

    @PostMapping("/slugcheck")
    public boolean isSlugClaimed(
            @RequestBody @Valid @NotNull @Size(min = 6) String slug) {
        return communityManageService.isSlugClaimed(slug);
    }
}
