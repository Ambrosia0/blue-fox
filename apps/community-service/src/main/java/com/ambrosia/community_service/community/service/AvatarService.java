package com.ambrosia.community_service.community.service;

import com.ambrosia.community_service.community.model.dto.request.FileMetadata;

public interface AvatarService {
    String upload(Long communityId, String avatarId, FileMetadata fileMetadata);
    void confirmUpload(Long communityId, String avatarId);
    void delete(Long communityId, String avatarId);
}
