package com.ambrosia.content_service.post.application;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.ambrosia.content_service.post.api.dto.request.PostCreateRequest;
import com.ambrosia.content_service.post.api.dto.request.PostEditRequest;
import com.ambrosia.content_service.post.api.dto.request.PostEditorFilter;
import com.ambrosia.content_service.post.api.dto.response.PostEditorContentResponse;
import com.ambrosia.content_service.post.api.dto.response.PostEditorViewResponse;
import com.ambrosia.library_policy.policy.Actor;

/**
 * Service which administrates use-cases over post operations
 * PostEditorService
 */
public interface PostEditorService {
    /**
     * Updates post
     * @param actor requesting actor
     * @param postId editted post id
     * @param postEditRequest dto 
     */
    void editPost(Actor actor, long postId, PostEditRequest postEditRequest);

    /**
     * Creates post
     * @param actor requesting actor
     * @param policy policy, which validates permissions for create operation
     * @param postCreateRequest 
     * @return
     */
    PostEditorViewResponse createPost(Actor actor, PostCreateRequest postCreateRequest);

    /**
     * Publishes post
     * @param policy policy, which validates permission for publish operation
     * @param postId
     * @param actor requesting actor
     */
    void publishPost(Actor actor, long postId);

    /**
     * Unpublishes post
     * @param actor requesting actor
     * @param postId unpublished post
     */
    void unpublishPost(Actor actor, long postId);

    /**
     * Deletes unpublished post
     * @param postId post
     * @param policy
     */
    void deleteDraftPost(long postId, Actor actor);

    /**
     * Returns content of unpublished post
     * @param actor requesting actor
     * @param userId author id
     * @return post content read projection
     */
    PostEditorContentResponse getContent(long postId, Actor actor);

    /**
     * Returns view of unpublished posts
     * @param actor requesting actor
     * @param filter filter
     * @return post view read projection
     */
    Slice<PostEditorViewResponse> getUnpublishedPosts(Actor actor, PostEditorFilter filter, Pageable pageable);
}
