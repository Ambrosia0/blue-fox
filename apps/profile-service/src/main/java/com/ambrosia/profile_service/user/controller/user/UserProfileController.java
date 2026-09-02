package com.ambrosia.profile_service.user.controller.user;

import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.profile_service.user.model.dto.request.FileMetadata;
import com.ambrosia.profile_service.user.model.dto.request.FirstLastName;
import com.ambrosia.profile_service.user.model.dto.request.SettingsRequest;
import com.ambrosia.profile_service.user.model.dto.response.AvatarUploadResponse;
import com.ambrosia.profile_service.user.model.dto.response.CurrentUserProfileResponse;
import com.ambrosia.profile_service.user.service.UserProfileService;
import com.ambrosia.profile_service.user.service.UserQueryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/me/profile")
@Validated
public class UserProfileController {
    private final UserProfileService userProfileService;

    private final UserQueryService userQueryService;

    @PatchMapping("/about")
    public void updateUserAboutText(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Size(max = 500) String text){
        userProfileService.setAboutText(UUID.fromString(jwt.getSubject()), text);
    }

    @PatchMapping("/username")
    public void updateUsername(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Size(min = 8, max = 32) String username){
        userProfileService.updateUsername(UUID.fromString(jwt.getSubject()), username);
    }

    @PutMapping("/avatar")
    public AvatarUploadResponse updateAvatar(
            @RequestBody(required = false) @Valid FileMetadata fileMetadata,
            @AuthenticationPrincipal Jwt jwt){
        return userProfileService.updateAvatar(UUID.fromString(jwt.getSubject()), fileMetadata);
    }

    @PatchMapping("/name")
    public void updateFirstLastName(
            @RequestBody @Valid FirstLastName firstLastName,
            @AuthenticationPrincipal Jwt jwt){
        userProfileService.updateFirstLastName(
            UUID.fromString(jwt.getSubject()),
            firstLastName
        );
    }

    @PostMapping("/avatar/{avatarId}")
    public void confirmAvatarUpload(
            @PathVariable String avatarId,
            @AuthenticationPrincipal Jwt jwt) {
        userProfileService.confirmAvatarUpload(
            UUID.fromString(jwt.getSubject()), 
            avatarId
        );
    }

    @GetMapping
    public CurrentUserProfileResponse getProfile(
            @AuthenticationPrincipal Jwt jwt){
        return userQueryService.getProfile(UUID.fromString(jwt.getSubject()));
    }

    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PutMapping("/settings")
    public void updateSettings(
            @RequestBody SettingsRequest settingsRequest,
            @AuthenticationPrincipal Jwt jwt){
        userProfileService.updateSettings(UUID.fromString(jwt.getSubject()), settingsRequest);
    }

}
