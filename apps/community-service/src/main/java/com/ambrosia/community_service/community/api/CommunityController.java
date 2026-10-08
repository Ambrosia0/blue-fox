package com.ambrosia.community_service.community.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.community_service.community.api.dto.request.CommunityCreate;
import com.ambrosia.community_service.community.api.dto.request.CommunityEdit;
import com.ambrosia.community_service.community.api.dto.request.FileMetadata;
import com.ambrosia.community_service.community.api.dto.response.AvatarUploadResponse;
import com.ambrosia.community_service.community.api.dto.response.CommunityCreateResponse;
import com.ambrosia.community_service.community.api.dto.response.CommunityEditResponse;
import com.ambrosia.community_service.community.application.CommunityManageService;
import com.ambrosia.community_service.community.application.CommunityModeratorService;
import com.ambrosia.library_policy.policy.Actor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/v1/community")
public class CommunityController {
    private final CommunityManageService communityManageService;

    private final CommunityModeratorService communityModeratorService;

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping
    public CommunityCreateResponse createCommunity(
            @RequestBody CommunityCreate createRequest,
            Actor actor) {
        return communityManageService.createCommunity(createRequest, actor);
    }
    

    @PatchMapping("/{id}")
    public CommunityEditResponse editCommunityInfo(
            @PathVariable long id,
            @RequestBody @Valid CommunityEdit communityEdit,
            Actor actor){
        return communityManageService.editCommunityInfo(
            id, 
            communityEdit, 
            actor
        );
    }

    @PutMapping("/{id}/avatar")
    public AvatarUploadResponse editCommunityAvatar(
            @PathVariable Long id,
            @RequestBody(required = false) FileMetadata fileMetadata,
            Actor actor) {
        return communityManageService.uploadAvatar(
            id, 
            fileMetadata,
            actor
        );
    }

    @PostMapping("/{id}/avatar/{avatarId}")
    public void confirmAvatarUpload(
            @PathVariable String avatarId,
            @PathVariable Long communityId
    ) {        
        communityManageService.validateAvatarUpload(
            communityId, 
            avatarId
        );
    }

    @DeleteMapping("/{id}")
    public void deleteCommunity(
        @PathVariable Long communityId,
        Actor actor
    ){
        communityManageService.deleteCommunity(communityId, null);
    }
    
    
    @PostMapping("/{id}/ban/{userId}")
    public void banUser(
            @PathVariable Long communityId,
            @PathVariable UUID userId,
            @RequestBody Instant beforeDate,
            Actor actor) {
        communityModeratorService.banUser(
            communityId, 
            actor, 
            userId,
            beforeDate
        );
    }

    @DeleteMapping("/{id}/ban/{userId}")
    public void unbanUser(
            @PathVariable Long communityId,
            @PathVariable UUID userId,
            Actor actor) {
        communityModeratorService.unbanUser(
            communityId, 
            actor, 
            userId
        );
    }
    
    @PostMapping("/slugcheck")
    public boolean isSlugClaimed(
            @RequestBody @Valid @NotNull @Size(min = 6) String slug) {
        return communityManageService.isSlugClaimed(slug);
    }
}
