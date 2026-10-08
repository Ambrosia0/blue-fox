package com.ambrosia.community_service.community.application;

import com.ambrosia.community_service.community.api.dto.request.FileMetadata;

public interface AvatarManager {
    String upload(Long communityId, String avatarId, FileMetadata fileMetadata);
    void confirmUpload(Long communityId, String avatarId);
    void delete(Long communityId, String avatarId);
}
