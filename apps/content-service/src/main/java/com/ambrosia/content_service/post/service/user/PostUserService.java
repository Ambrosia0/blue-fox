package com.ambrosia.content_service.post.service.user;

import java.util.List;
import java.util.UUID;

import com.ambrosia.content_service.post.model.dto.response.PostContentResponse;
import com.ambrosia.content_service.post.model.dto.response.PreviewWithScoreResponse;
import com.ambrosia.content_service.search.model.dto.EventFilter;

import jakarta.annotation.Nullable;

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
    PostContentResponse getPost(long id, @Nullable UUID requestingUserId);
    
    /**
     * Returns searched posts
     * @param eventFilter filter for searched content
     * @param requestingUserId Id of the requesting user
     * @param pageSize page size
     * @return search read model
     */
    List<PreviewWithScoreResponse> search(EventFilter eventFilter, @Nullable UUID requestingUserId, int pageSize);

    boolean isAuthor(long postId, UUID userId);
    boolean isExists(long postId);
}
