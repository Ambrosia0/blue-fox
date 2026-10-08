package com.ambrosia.content_service.post.application.query;

import java.util.List;
import java.util.UUID;

import com.ambrosia.content_service.post.api.dto.response.PostContentResponse;
import com.ambrosia.content_service.post.api.dto.response.PostViewResponse;

import jakarta.annotation.Nullable;

/**
 * Service for published posts read operations
 * PostQueryService
 */
public interface PostViewQueryService {
    /**
     * Returns published posts with content
     * @param postId post id
     * @return post content view model
     */
    PostContentResponse getPost(long postId);

    /**
     * Returns view model of published posts
     * @param ids post ids
     * @return post view model
     */
    List<PostViewResponse> getPostPreviews(Iterable<Long> ids, @Nullable UUID userId);

    /**
     * Returns preview of published post
     * @param postId post id
     * @return post view model
     */
    PostViewResponse getPostPreview(long postId, @Nullable UUID userId);
}
