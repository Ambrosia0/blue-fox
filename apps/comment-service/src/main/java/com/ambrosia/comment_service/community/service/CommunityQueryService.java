package com.ambrosia.comment_service.community.service;

import java.util.Optional;
import java.util.UUID;

import com.ambrosia.comment_service.community.model.dto.CommunityUserData;

public interface CommunityQueryService {
    Optional<CommunityUserData> findCommunityUserDataByPostId(long postId, UUID userId);
    Optional<CommunityUserData> findCommunityUserDataByCommentId(long commentId, UUID userId);
}
