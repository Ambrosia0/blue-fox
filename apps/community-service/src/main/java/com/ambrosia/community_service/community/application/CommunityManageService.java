package com.ambrosia.community_service.community.application;

import com.ambrosia.community_service.community.api.dto.request.CommunityCreate;
import com.ambrosia.community_service.community.api.dto.request.CommunityEdit;
import com.ambrosia.community_service.community.api.dto.request.FileMetadata;
import com.ambrosia.community_service.community.api.dto.response.AvatarUploadResponse;
import com.ambrosia.community_service.community.api.dto.response.CommunityCreateResponse;
import com.ambrosia.community_service.community.api.dto.response.CommunityEditResponse;
import com.ambrosia.library_policy.policy.Actor;

public interface CommunityManageService {
    CommunityCreateResponse createCommunity(
        CommunityCreate communityCreate, 
        Actor actor
    );
    CommunityEditResponse editCommunityInfo(
        long communityId, 
        CommunityEdit communityEdit, 
        Actor actor
    );
    AvatarUploadResponse uploadAvatar(
        long communityId, 
        FileMetadata fileMetadata,
        Actor actor
    );
    void validateAvatarUpload(
        long communityId,
        String avatarId
    );
    void deleteCommunity(
        long communityId,
        Actor actor
    );
    boolean isSlugClaimed(String slug);
}
