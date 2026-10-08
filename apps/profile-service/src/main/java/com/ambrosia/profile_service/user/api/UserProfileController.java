package com.ambrosia.profile_service.user.api;

import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.profile_service.user.api.dto.request.FileMetadata;
import com.ambrosia.profile_service.user.api.dto.request.FirstLastName;
import com.ambrosia.profile_service.user.api.dto.request.SettingsRequest;
import com.ambrosia.profile_service.user.api.dto.response.AvatarUploadResponse;
import com.ambrosia.profile_service.user.api.dto.response.CurrentUserProfileResponse;
import com.ambrosia.profile_service.user.application.UserProfileService;
import com.ambrosia.profile_service.user.application.query.UserQueryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/v1/me/profile")
@Validated
public class UserProfileController {
    private final UserProfileService userProfileService;

    private final UserQueryService userQueryService;

    @PatchMapping("/about")
    public void updateUserAboutText(
            Actor actor,
            @RequestBody @Size(max = 500) String text){
        userProfileService.setAboutText(actor, text);
    }

    @PatchMapping("/username")
    public void updateUsername(
            Actor actor,
            @RequestBody @Size(min = 8, max = 32) String username){
        userProfileService.updateUsername(actor, username);
    }

    @PutMapping("/avatar")
    public AvatarUploadResponse updateAvatar(
            @RequestBody(required = false) @Valid FileMetadata fileMetadata,
            Actor actor){
        return userProfileService.updateAvatar(actor, fileMetadata);
    }

    @PatchMapping("/name")
    public void updateFirstLastName(
            @RequestBody @Valid FirstLastName firstLastName,
            Actor actor){
        userProfileService.updateFirstLastName(
            actor,
            firstLastName
        );
    }

    @PostMapping("/avatar/{avatarId}")
    public void confirmAvatarUpload(
            @PathVariable String avatarId,
            Actor actor) {
        userProfileService.confirmAvatarUpload(
            actor, 
            avatarId
        );
    }

    @GetMapping
    public CurrentUserProfileResponse getProfile(Actor actor){
        return userQueryService.getProfile(actor);
    }

    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PutMapping("/settings")
    public void updateSettings(
            @RequestBody SettingsRequest settingsRequest,
            Actor actor){
        userProfileService.updateSettings(actor, settingsRequest);
    }

}
