package com.ambrosia.content_service.post.service.user;

import java.util.List;

import com.ambrosia.content_service.post.model.dto.response.PostContentResponse;
import com.ambrosia.content_service.post.model.dto.response.PreviewWithScoreResponse;
import com.ambrosia.content_service.post.utils.policy.PostPolicy;
import com.ambrosia.content_service.search.model.dto.EventFilter;

/**
 * Service which administrates post view use-cases
 * PostUserService
 */
public interface PostUserService {
    /**
     * Returns post content
     * @param id post id
     * @param requestingUserId Id of the requesting user
     * @return post read model
     */
    PostContentResponse getPost(long id, PostPolicy policy);
    
    /**
     * Returns searched posts
     * @param eventFilter filter for searched content
     * @param requestingUserId Id of the requesting user
     * @param pageSize page size
     * @return search read model
     */
    List<PreviewWithScoreResponse> search(EventFilter eventFilter, PostPolicy policy, int pageSize);

    /**
     * Deletes published post
     * @param postId deleted post
     * @param policy policy
     */
    void deletePost(long postId, PostPolicy policy);
}
