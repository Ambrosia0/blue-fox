package com.ambrosia.content_service.post.service.impl;

import java.util.Collection;

import org.springframework.stereotype.Service;

import com.ambrosia.content_service.kafka_events.AggregatedPreviewEvent;
import com.ambrosia.content_service.kafka_events.AggregatedViewEvent;
import com.ambrosia.content_service.kafka_events.PostDelta;
import com.ambrosia.content_service.post.repository.PostRepository;
import com.ambrosia.content_service.post.service.PostInternalService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostInternalServiceImpl implements PostInternalService{
    private final PostRepository postRepository;
    
    @Override
    public void incrementViewCount(Collection<AggregatedViewEvent> toIncrement) {
        postRepository.batchViewIncrement(toIncrement);
    }

    @Override
    public void incrementPreviewCount(Collection<AggregatedPreviewEvent> toIncrement) {
        postRepository.batchPreviewIncrement(toIncrement);
    }
    
    @Override
    public void incrementCommentCount(Collection<PostDelta> toIncrement) {
        postRepository.batchCommentCountIncrement(toIncrement);
    }
}
