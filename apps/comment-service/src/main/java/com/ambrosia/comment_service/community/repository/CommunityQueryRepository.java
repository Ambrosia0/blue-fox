package com.ambrosia.comment_service.community.repository;

import java.util.Optional;
import java.util.UUID;

import com.ambrosia.comment_service.community.model.dto.CommentUserData;
import com.ambrosia.comment_service.community.model.dto.CommunityUserData;

public interface CommunityQueryRepository {
    Optional<CommunityUserData> findCommunityUserDataByPostId(long postId, UUID userId);
    Optional<CommunityUserData> findCommunityUserDataByCommentId(long commentId, UUID userId);
    Optional<CommentUserData> findCommentUserDataByCommentId(long commentId, UUID userId);
}
