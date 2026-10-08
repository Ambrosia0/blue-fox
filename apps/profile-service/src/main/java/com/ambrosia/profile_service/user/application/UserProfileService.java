package com.ambrosia.profile_service.user.application;

import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.profile_service.user.api.dto.request.FileMetadata;
import com.ambrosia.profile_service.user.api.dto.request.FirstLastName;
import com.ambrosia.profile_service.user.api.dto.request.SettingsRequest;
import com.ambrosia.profile_service.user.api.dto.response.AvatarUploadResponse;

import jakarta.annotation.Nullable;

public interface UserProfileService {
    void setAboutText(Actor actor, String text);
    void updateUsername(Actor actor, String username);
    void updateFirstLastName(Actor actor, FirstLastName firstLastName);
    AvatarUploadResponse updateAvatar(Actor actor, @Nullable FileMetadata fileMetadata);
    void confirmAvatarUpload(Actor actor, String avatarId);
    void updateSettings(Actor actor, SettingsRequest settingsRequest);
}
