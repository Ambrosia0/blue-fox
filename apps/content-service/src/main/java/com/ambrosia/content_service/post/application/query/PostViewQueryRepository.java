package com.ambrosia.content_service.post.application.query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ambrosia.content_service.post.api.dto.response.PostContentResponse;
import com.ambrosia.content_service.post.api.dto.response.PostViewResponse;

import jakarta.annotation.Nullable;

public interface PostViewQueryRepository {
    Optional<PostContentResponse> findPublishedByPostId(long postId);
    List<PostViewResponse> findPreviewsByIds(Iterable<Long> postIds, @Nullable UUID requestingUser);
    Optional<PostViewResponse> findPreviewById(long postId, @Nullable  UUID requestingUser);
}
