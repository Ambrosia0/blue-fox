package com.ambrosia.content_service.post.service.user;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ambrosia.content_service.post.model.dto.request.PostCreateRequest;
import com.ambrosia.content_service.post.model.dto.request.PostEditRequest;
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
     * @param userId id of the author
     * @param policy policy, which validates permission for publish operation
     * @param postId
     */
    void publishPost(UUID authorId, PostPolicy policy, long postId);

    /**
     * Deletes post
     * @param postId post
     * @param policy
     */
    void deletePost(long postId, PostPolicy policy);

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
     * @param pageable pageable
     * @return post view read projection
     */
    Page<PostEditorViewResponse> getUnpublishedPosts(UUID authorId, Pageable pageable);
}
