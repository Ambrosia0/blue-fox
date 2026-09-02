package com.ambrosia.report_service.comment.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.comment_service.kafka_events.CommentCreated;
import com.ambrosia.comment_service.kafka_events.CommentDeleted;
import com.ambrosia.report_service.comment.entity.CommentProjection;
import com.ambrosia.report_service.comment.repository.CommentProjectionRepository;
import com.ambrosia.report_service.comment.service.CommentProjectionService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentProjectionServiceImpl implements CommentProjectionService{
    private final CommentProjectionRepository commentProjectionRepository;

    @Override
    public void create(CommentCreated commentCreated, UUID eventId) {
        commentProjectionRepository.insert(
            CommentProjection.create(commentCreated.getId()),
            eventId
        );
    }

    @Override
    public void delete(CommentDeleted commentDeleted, UUID eventId) {
        commentProjectionRepository.delete(
            commentDeleted.getId(),
            eventId
        );
    }

    @Override
    public boolean exist(Long id) {
        return commentProjectionRepository.existsById(id);
    }
}
