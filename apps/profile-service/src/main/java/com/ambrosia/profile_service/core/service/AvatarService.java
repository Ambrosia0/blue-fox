package com.ambrosia.profile_service.core.service;

import java.util.UUID;

import com.ambrosia.profile_service.user.model.dto.request.FileMetadata;

public interface AvatarService {
    String upload(UUID userId, String avatarId, FileMetadata fileMetadata);
    boolean validateUpload(UUID userId, String avatarId);
    void delete(UUID userId, String avatarId);
}
