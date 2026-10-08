package com.ambrosia.content_service.post.application;

import java.util.List;

import com.ambrosia.content_service.post.api.dto.EventFilter;
import com.ambrosia.content_service.post.api.dto.response.PostContentResponse;
import com.ambrosia.content_service.post.api.dto.response.PreviewWithScoreResponse;
import com.ambrosia.library_policy.policy.Actor;

/**
 * Service which administrates post view use-cases
 * PostUserService
 */
public interface PostUserService {
    /**
     * Returns post content
     * @param id post id
     * @param actor requesting actor
     * @return post read model
     */
    PostContentResponse getPost(long id, Actor actor);
    
    /**
     * Returns searched posts
     * @param eventFilter filter for searched content
     * @param actor requesting actor
     * @param pageSize page size
     * @return search read model
     */
    List<PreviewWithScoreResponse> search(EventFilter eventFilter, Actor actor, int pageSize);

    /**
     * Deletes published post
     * @param postId deleted post
     * @param actor requesting actor
     */
    void deletePost(long postId, Actor actor);
}
