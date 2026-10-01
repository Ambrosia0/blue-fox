package com.ambrosia.content_service.post.service.user;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.ambrosia.content_service.post.model.dto.request.PostCreateRequest;
import com.ambrosia.content_service.post.model.dto.request.PostEditRequest;
import com.ambrosia.content_service.post.model.dto.request.PostEditorFilter;
import com.ambrosia.content_service.post.model.dto.response.PostEditorContentResponse;
import com.ambrosia.content_service.post.model.dto.response.PostEditorViewResponse;
import com.ambrosia.content_service.post.utils.policy.PostPolicy;

/**
 * Service which administrates use-cases over post operations
 * PostEditorService
 */
public interface PostEditorService {
    /**
     * Updates post
     * @param authorId requesting user id
     * @param postId editted post id
     * @param postEditRequest dto 
     */
    void editPost(UUID authorId, long postId, PostEditRequest postEditRequest);

    /**
     * Creates post
     * @param authorId requesting user id
     * @param policy policy, which validates permissions for create operation
     * @param postCreateRequest 
     * @return
     */
    PostEditorViewResponse createPost(UUID authorId, PostPolicy policy, PostCreateRequest postCreateRequest);

    /**
     * Publishes post
     * @param policy policy, which validates permission for publish operation
     * @param postId
     */
    void publishPost(PostPolicy policy, long postId);

    /**
     * Unpublishes post
     * @param authorId id of the author
     * @param postId unpublished post
     */
    void unpublishPost(UUID authorId, long postId);

    /**
     * Deletes unpublished post
     * @param postId post
     * @param policy
     */
    void deleteDraftPost(long postId, PostPolicy policy);

    /**
     * Returns content of unpublished post
     * @param authorId post id
     * @param userId author id
     * @return post content read projection
     */
    PostEditorContentResponse getContent(long postId, UUID authorId);

    /**
     * Returns view of unpublished posts
     * @param authorId author id
     * @param filter filter
     * @return post view read projection
     */
    Slice<PostEditorViewResponse> getUnpublishedPosts(UUID authorId, PostEditorFilter filter, Pageable pageable);
}
