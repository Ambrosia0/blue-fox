package com.ambrosia.report_service.post.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.content_service.kafka_events.PostCreated;
import com.ambrosia.content_service.kafka_events.PostDeleted;
import com.ambrosia.report_service.post.entity.PostProjection;
import com.ambrosia.report_service.post.repository.PostProjectionRepository;
import com.ambrosia.report_service.post.service.PostProjectionService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostProjectionServiceImpl implements PostProjectionService{
    private final PostProjectionRepository postProjectionRepository;

    @Override
    public void create(PostCreated postCreated, UUID eventId) {
        postProjectionRepository.insert(
            PostProjection.create(postCreated.getId()),
            eventId
        );
    }

    @Override
    public void delete(PostDeleted postDeleted, UUID eventId) {
        postProjectionRepository.delete(
            postDeleted.getId(),
            eventId
        );
    }

    @Override
    public boolean exist(Long id) {
        return postProjectionRepository.existsById(id);
    }
}
