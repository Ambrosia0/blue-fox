package com.ambrosia.comment_service.post.repository;

import java.util.List;

import com.ambrosia.content_service.kafka_events.PostEvent;

public interface CustomPostProjectionRepository {
    void batchProcess(List<PostEvent> toInsert, List<PostEvent> toDelete);
}
