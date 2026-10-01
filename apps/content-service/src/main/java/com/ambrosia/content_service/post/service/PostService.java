package com.ambrosia.content_service.post.service;

import java.util.UUID;

public interface PostService {
    boolean isAuthor(long postId, UUID userId);
    boolean isExists(long postId);
}
