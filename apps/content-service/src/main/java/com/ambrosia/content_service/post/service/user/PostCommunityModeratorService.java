package com.ambrosia.content_service.post.service.user;

import java.util.UUID;

public interface PostCommunityModeratorService {
    void deletePost(UUID requestingUser, long postId);
}
