package com.ambrosia.content_service.post.application.query.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ambrosia.content_service.post.api.dto.response.PostContentResponse;
import com.ambrosia.content_service.post.api.dto.response.PostViewResponse;
import com.ambrosia.content_service.post.application.query.PostViewQueryRepository;
import com.ambrosia.content_service.post.application.query.PostViewQueryService;
import com.ambrosia.content_service.post.exception.PostDoesntExistException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostViewQueryServiceImpl implements PostViewQueryService{
    private final PostViewQueryRepository postQueryRepository;
    
    @Cacheable(cacheNames = "posts", key = "#postId")
    @Override
    public PostContentResponse getPost(long postId) {
        return postQueryRepository.findPublishedByPostId(postId)
            .orElseThrow(() -> new PostDoesntExistException());
    }

    @Override
    public List<PostViewResponse> getPostPreviews(Iterable<Long> ids, UUID userId) {
        return postQueryRepository.findPreviewsByIds(ids, userId);
    }

    public PostViewResponse getPostPreview(long postId, UUID userId){
        return postQueryRepository.findPreviewById(postId, userId)
            .orElseThrow(() -> new PostDoesntExistException());
    }
}
