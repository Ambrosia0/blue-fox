package com.ambrosia.comment_service.post.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ambrosia.comment_service.post.model.entity.PostProjection;
import com.ambrosia.comment_service.post.repository.PostProjectionRepository;
import com.ambrosia.comment_service.post.service.PostProjectionCreator;
import com.ambrosia.comment_service.post.service.PostProjectionService;
import com.ambrosia.content_service.kafka_events.PostEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostProjectionServiceImpl implements PostProjectionCreator, PostProjectionService{
    private final PostProjectionRepository postProjectionRepository;

    @Override
    public void process(List<PostEvent> toInsert, List<PostEvent> toDelete){
        postProjectionRepository.batchProcess(toInsert, toDelete);
    }

    @Override
    public boolean isExists(long id) {
        return postProjectionRepository.existsById(id);
    }

    @Cacheable(cacheNames = "comment-post-projection", key = "#postId", unless = "#result == null")
    @Override
    public Optional<PostProjection> findProjectionById(long postId) {
        return postProjectionRepository.findById(postId);
    }
}
