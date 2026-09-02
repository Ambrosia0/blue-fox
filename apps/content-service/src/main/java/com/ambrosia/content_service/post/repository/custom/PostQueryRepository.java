package com.ambrosia.content_service.post.repository.custom;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ambrosia.content_service.post.model.dto.response.PostContentResponse;
import com.ambrosia.content_service.post.model.dto.response.PostViewResponse;

public interface PostQueryRepository {
    Optional<PostContentResponse> findPublishedByPostId(long postId);
    List<PostViewResponse> findPreviewsByIdInList(List<Long> postIds);
    List<PostViewResponse> findPreviewsByIdInListWithLike(List<Long> postIds, UUID requestingUser);
}
