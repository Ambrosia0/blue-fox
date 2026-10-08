package com.ambrosia.report_service.comment.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.comment_service.kafka_events.CommentEvent;
import com.ambrosia.report_service.comment.repository.CommentProjectionRepository;
import com.ambrosia.report_service.comment.service.CommentProjectionService;
import com.ambrosia.report_service.comment.service.mapper.CommentProjectionMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentProjectionServiceImpl implements CommentProjectionService{
    private final CommentProjectionRepository commentProjectionRepository;

    private final CommentProjectionMapper commentProjectionMapper;

    @Override
    public void process(CommentEvent commentEvent) {
        var eventId = UUID.fromString(commentEvent.getEventId());
        var commentId = commentEvent.getCommentId();
        switch (commentEvent.getEventCase()) {
            case CREATED ->{
                commentProjectionRepository.insert(
                    commentProjectionMapper.toEntity(commentId, commentEvent.getCreated()), 
                    eventId
                );
            }
            case DELETED ->{
                commentProjectionRepository.delete(commentId, eventId);
            }
            default ->{}
        }
    }

    @Override
    public boolean exist(Long id) {
        return commentProjectionRepository.existsById(id);
    }
}
