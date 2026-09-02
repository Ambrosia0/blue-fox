package com.ambrosia.profile_service.user.service;

import java.util.List;
import java.util.UUID;

import com.ambrosia.profile_service.core.UserInfo;
import com.ambrosia.profile_service.user.model.dto.request.FileMetadata;
import com.ambrosia.profile_service.user.model.dto.request.FirstLastName;
import com.ambrosia.profile_service.user.model.dto.request.SettingsRequest;
import com.ambrosia.profile_service.user.model.dto.response.AvatarUploadResponse;

import jakarta.annotation.Nullable;

public interface UserProfileService {
    void setAboutText(UUID id, String text);
    void updateUsername(UUID userId, String username);
    void updateFirstLastName(UUID userId, FirstLastName firstLastName);
    AvatarUploadResponse updateAvatar(UUID userId, @Nullable FileMetadata fileMetadata);
    void confirmAvatarUpload(UUID userId, String avatarId);
    List<UserInfo> getUserInfo(List<UUID> ids);
    void updateSettings(UUID userId, SettingsRequest settingsRequest);
}
