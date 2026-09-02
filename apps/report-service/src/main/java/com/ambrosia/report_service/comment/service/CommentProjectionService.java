package com.ambrosia.report_service.comment.service;

import java.util.UUID;

import com.ambrosia.comment_service.kafka_events.CommentCreated;
import com.ambrosia.comment_service.kafka_events.CommentDeleted;

public interface CommentProjectionService {
    void create(CommentCreated commentCreated, UUID eventId);
    void delete(CommentDeleted commentDeleted, UUID eventId);
    boolean exist(Long id);
}
