package com.ambrosia.content_service.post.service;

import java.util.List;
import java.util.UUID;

import com.ambrosia.content_service.post.model.dto.response.PostContentResponse;
import com.ambrosia.content_service.post.model.dto.response.PostViewResponse;

/**
 * Service for post read operations
 * PostQueryService
 */
public interface PostQueryService {
    /**
     * Returns published posts with content
     * @param postId post id
     * @return post content view model
     */
    PostContentResponse getPublishedPostWithCommunity(long postId);

    /**
     * Returns view model of published posts
     * @param ids post ids
     * @return post view model
     */
    List<PostViewResponse> getPostPreviewsByIds(List<Long> ids, UUID userId);
}
