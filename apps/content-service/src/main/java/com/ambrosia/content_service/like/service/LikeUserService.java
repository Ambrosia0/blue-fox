package com.ambrosia.content_service.like.service;

import java.util.UUID;

public interface LikeUserService {
    void likePost(long postId, UUID userId);
    void unlikePost(long postId, UUID userId);
    boolean isLiked(long postId, UUID userId);
}
