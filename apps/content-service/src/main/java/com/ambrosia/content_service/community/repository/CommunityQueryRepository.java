package com.ambrosia.content_service.community.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ambrosia.content_service.community.model.dto.CommunityUserData;
import com.ambrosia.content_service.community.model.dto.PostUserData;

import jakarta.annotation.Nullable;

public interface CommunityQueryRepository {
    Optional<CommunityUserData> findCommunityUserDataByCommunityId(long communityId, @Nullable UUID userId);
    List<CommunityUserData> findCommunityUserDataByReplyId(UUID userId, @Nullable Long communityId, Long postId);
    Optional<PostUserData> findPostUserDataByPostId(long postId, @Nullable UUID userId);
}
