package com.ambrosia.report_service.post.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.content_service.kafka_events.PostEvent;
import com.ambrosia.report_service.post.repository.PostProjectionRepository;
import com.ambrosia.report_service.post.service.PostProjectionService;
import com.ambrosia.report_service.post.service.mapper.PostProjectionMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostProjectionServiceImpl implements PostProjectionService{
    private final PostProjectionRepository postProjectionRepository;

    private final PostProjectionMapper postProjectionMapper;

    @Override
    public void process(PostEvent postEvent) {
        var eventId = UUID.fromString(postEvent.getEventId());
        var postId = postEvent.getPostId();
        switch (postEvent.getEventCase()) {
            case CREATED -> {
                postProjectionRepository.insert(
                    postProjectionMapper.toEntity(postId, postEvent.getCreated()), 
                    eventId
                );
            }
            case DELETED -> {
                postProjectionRepository.delete(postId, eventId);
            }
            default -> {}
        }
    }

    @Override
    public boolean exist(Long id) {
        return postProjectionRepository.existsById(id);
    }
}
