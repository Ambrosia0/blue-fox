package com.ambrosia.report_service.comment.service;

import com.ambrosia.comment_service.kafka_events.CommentEvent;

public interface CommentProjectionService {
    boolean exist(Long id);
}
