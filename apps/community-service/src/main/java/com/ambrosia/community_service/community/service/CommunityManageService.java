package com.ambrosia.community_service.community.service;

import java.util.List;
import java.util.UUID;

import com.ambrosia.community_service.community.model.dto.request.CommunityCreate;
import com.ambrosia.community_service.community.model.dto.request.CommunityEdit;
import com.ambrosia.community_service.community.model.dto.request.FileMetadata;
import com.ambrosia.community_service.community.model.dto.request.ScopePair;
import com.ambrosia.community_service.community.model.dto.response.AvatarUploadResponse;
import com.ambrosia.community_service.community.model.dto.response.CommunityResponse;
import com.ambrosia.community_service.community.utils.policy.CommunityAccessPolicy;

public interface CommunityManageService {
    CommunityResponse createCommunity(
        CommunityCreate communityCreate, 
        UUID userId
    );
    CommunityResponse editCommunityInfo(
        long communityId, 
        CommunityEdit communityEdit, 
        CommunityAccessPolicy policy
    );
    AvatarUploadResponse uploadAvatar(
        long communityId, 
        FileMetadata fileMetadata,
        CommunityAccessPolicy policy
    );
    void validateAvatarUpload(
        long communityId,
        String avatarId,
        CommunityAccessPolicy policy
    );
    void editCommunityScopes(
        long communityId,
        List<ScopePair> userScopes,
        CommunityAccessPolicy policy
    );
    void deleteCommunity(
        long communityId,
        CommunityAccessPolicy policy
    );
    boolean isSlugClaimed(String slug);
}
