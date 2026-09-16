package com.ambrosia.content_service.post.service.user;

import java.util.UUID;

/**
 * Service which administrates use-cases community moderator use-cases
 * PostCommunityModeratorService
 */
public interface PostCommunityModeratorService {
    /**
     * Deletes linked to community post
     * @param requestingUserId requesting user id
     * @param postId Id of the post to delete
     */
    void deletePost(UUID requestingUserId, long postId);
}
