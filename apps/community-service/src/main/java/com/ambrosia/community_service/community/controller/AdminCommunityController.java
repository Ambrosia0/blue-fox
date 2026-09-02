package com.ambrosia.community_service.community.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.community_service.community.model.dto.request.CommunityEdit;
import com.ambrosia.community_service.community.model.dto.request.FileMetadata;
import com.ambrosia.community_service.community.model.dto.request.ScopePair;
import com.ambrosia.community_service.community.model.dto.response.AvatarUploadResponse;
import com.ambrosia.community_service.community.model.dto.response.CommunityResponse;
import com.ambrosia.community_service.community.model.entity.Community;
import com.ambrosia.community_service.community.service.CommunityManageService;
import com.ambrosia.community_service.community.service.admin.AdminCommunityService;
import com.ambrosia.community_service.community.utils.policy.AdminActor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RequiredArgsConstructor
@Validated
@RequestMapping("/api/admin/community")
@RestController
public class AdminCommunityController {
    private final CommunityManageService communityManageService;

    private final AdminCommunityService adminCommunityService;
    
    @GetMapping
    public Page<Community> getCommunities(Pageable pageable) {
        return adminCommunityService.getCommunities(pageable);
    }

    @DeleteMapping("/{id}")
    public void deleteCommunity(@PathVariable Long communityId){
        communityManageService.deleteCommunity(
            communityId,
            new AdminActor()
        );
    }

    @PatchMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommunityResponse editCommunityInfo(
        @PathVariable Long communityId,
        @RequestBody @Valid CommunityEdit adminCommunityEdit){
        return communityManageService.editCommunityInfo(
            communityId,
            adminCommunityEdit,
            new AdminActor()
        );
    }

    @PutMapping("/{id}/avatar")
    public AvatarUploadResponse editCommunityAvatar(
        @PathVariable Long id,
        @RequestBody FileMetadata fileMetadata) {
        return communityManageService.uploadAvatar(
            id,
            fileMetadata,
            new AdminActor()
        );
    }

    @PostMapping("/{id}/avatar/{avatarId}")
    public void confirmAvatarUpload(
            @PathVariable Long id,
            @PathVariable String avatarId) {
        communityManageService.validateAvatarUpload(
            id,
            avatarId,
            new AdminActor()
        );
    }
    
    
    @PutMapping("/{id}/scopes")
    public void editCommunityScopes(
        @PathVariable Long id, 
        @RequestBody @Valid @Size(max = 3) List<@Valid ScopePair> scopes) {
        communityManageService.editCommunityScopes(
            id,
            scopes,
            new AdminActor()
        );
    }


}
