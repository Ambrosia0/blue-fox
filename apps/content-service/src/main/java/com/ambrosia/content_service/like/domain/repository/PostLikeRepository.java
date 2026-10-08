package com.ambrosia.content_service.like.domain.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.ambrosia.content_service.like.infrastructure.entity.PostLike;

public interface PostLikeRepository{
    boolean existsById(UUID userId, long postId);
    void deleteById(UUID userId, long postId);
    PostLike findById(UUID userId, long postId);
    Set<Long> findLikedPostIdsByPostIds(List<Long> postIds, UUID requestingUser);
    int save(UUID userId, Long postId);
    int delete(UUID userId, Long postId);
}
