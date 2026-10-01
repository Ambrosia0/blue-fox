package com.ambrosia.content_service.post.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ambrosia.content_service.post.exception.PostDoesntExistException;
import com.ambrosia.content_service.post.model.dto.response.PostContentResponse;
import com.ambrosia.content_service.post.model.dto.response.PostViewResponse;
import com.ambrosia.content_service.post.repository.PostViewQueryRepository;
import com.ambrosia.content_service.post.service.PostViewQueryService;

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
