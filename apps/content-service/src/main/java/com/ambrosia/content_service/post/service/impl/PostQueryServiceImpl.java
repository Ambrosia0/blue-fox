package com.ambrosia.content_service.post.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ambrosia.content_service.post.exception.PostDoesntExistException;
import com.ambrosia.content_service.post.model.dto.response.PostContentResponse;
import com.ambrosia.content_service.post.model.dto.response.PostViewResponse;
import com.ambrosia.content_service.post.repository.custom.PostQueryRepository;
import com.ambrosia.content_service.post.service.PostQueryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostQueryServiceImpl implements PostQueryService{
    private final PostQueryRepository postQueryRepository;
    
    @Cacheable(cacheNames = "posts", key = "#postId")
    @Override
    public PostContentResponse getPublishedPostWithCommunity(long postId) {
        return postQueryRepository.findPublishedByPostId(postId)
            .orElseThrow(() -> new PostDoesntExistException());
    }

    @Override
    public List<PostViewResponse> getPostPreviewsByIds(List<Long> ids) {
        return postQueryRepository.findPreviewsByIdInList(ids);
    }

    @Override
    public List<PostViewResponse> getPostPreviewsByIdsWithLike(List<Long> ids, UUID userId) {
        return postQueryRepository.findPreviewsByIdInListWithLike(ids, userId);
    }
}
