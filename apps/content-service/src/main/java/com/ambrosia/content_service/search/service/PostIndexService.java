package com.ambrosia.content_service.search.service;

import com.ambrosia.content_service.search.model.dto.PostIndex;

public interface PostIndexService {
    void index(PostIndex postIndex);
    void reIndex(PostIndex postIndex);
    void deleteFromIndex(Long postId);
}
