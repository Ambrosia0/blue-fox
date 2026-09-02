package com.ambrosia.content_service.post.service.user;

import java.util.List;
import java.util.UUID;

import com.ambrosia.content_service.post.model.dto.response.PostContentResponse;
import com.ambrosia.content_service.post.model.dto.response.PreviewWithScoreResponse;
import com.ambrosia.content_service.search.model.dto.EventFilter;

import jakarta.annotation.Nullable;

public interface PostUserService {
    PostContentResponse getPost(long id, @Nullable UUID requestingUser);
    List<PreviewWithScoreResponse> search(EventFilter eventFilter, @Nullable UUID requestingUser, int pageSize);
    boolean isAuthor(long postId, UUID userId);
    boolean isExists(long postId);
}
