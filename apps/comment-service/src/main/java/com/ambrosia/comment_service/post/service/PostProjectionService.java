package com.ambrosia.comment_service.post.service;

import java.util.Optional;

import com.ambrosia.comment_service.post.model.entity.PostProjection;

public interface PostProjectionService {
    boolean isExists(long id);
    Optional<PostProjection> findProjectionById(long postId);
    
}
