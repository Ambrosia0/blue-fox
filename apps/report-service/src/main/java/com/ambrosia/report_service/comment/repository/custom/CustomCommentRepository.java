package com.ambrosia.report_service.comment.repository.custom;

import com.ambrosia.comment_service.kafka_events.CommentEvent;

import java.util.List;

public interface CustomCommentRepository {
    void process(List<CommentEvent> toUpsert, List<CommentEvent> toDelete);
}
