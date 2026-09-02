package com.ambrosia.content_service.search.service;

import com.ambrosia.content_service.post.model.entity.Post;

public interface PostIndexService {
    void index(Post post);
    void reIndex(Post post);
    void deleteFromIndex(Long postId);
}
