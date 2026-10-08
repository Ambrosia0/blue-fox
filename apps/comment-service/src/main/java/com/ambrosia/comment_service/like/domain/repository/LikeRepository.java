package com.ambrosia.comment_service.like.domain.repository;

import java.util.UUID;

public interface LikeRepository{
    void add(UUID userId, Long postId);
    void remove(UUID userId, Long postId);
}
