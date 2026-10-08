package com.ambrosia.content_service.post.application.query;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.ambrosia.content_service.post.api.dto.request.PostEditorFilter;
import com.ambrosia.content_service.post.api.dto.response.PostEditorContentResponse;
import com.ambrosia.content_service.post.api.dto.response.PostEditorViewResponse;

/**
 * Service for unpublished posts read operations
 * PostQueryService
 */
public interface PostEditorQueryService {
    /**
     * Returns view model of unpublished post for user
     * @param postId post id
     * @param userId user id
     * @return post editor content view model
     */
    Optional<PostEditorContentResponse> getPostContent(long postId, UUID userId);

    /**
     * Returns view model of specific unpublished post for user
     * @param userId user id
     * @return
     */
    Optional<PostEditorViewResponse> getPostPreview(long postId, UUID userId);
    
    /**
     * Returns view model of unpublished posts for user
     * @param userId
     * @return post editor preview view model
     */
    Slice<PostEditorViewResponse> getPostsPreview(UUID userId, PostEditorFilter filter, Pageable pageable);
}
