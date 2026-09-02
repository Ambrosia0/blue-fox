package com.ambrosia.comment_service.community.service;

import java.util.UUID;

public interface CommunityPermissionService {
    void validateCommentCreate(UUID userId, long postId);
    void validateCommentView(UUID userId, long postId);
    void validateCommentTreeView(UUID userId, long commentId);
    void validateCommentLike(UUID userId, long commentId);
}
