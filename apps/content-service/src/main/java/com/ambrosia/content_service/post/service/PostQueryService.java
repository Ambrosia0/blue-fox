package com.ambrosia.content_service.post.service;

import java.util.List;
import java.util.UUID;

import com.ambrosia.content_service.post.model.dto.response.PostContentResponse;
import com.ambrosia.content_service.post.model.dto.response.PostViewResponse;

public interface PostQueryService {
    PostContentResponse getPublishedPostWithCommunity(long postId);
    List<PostViewResponse> getPostPreviewsByIds(List<Long> ids);
    List<PostViewResponse> getPostPreviewsByIdsWithLike(List<Long> ids, UUID userId);
}
