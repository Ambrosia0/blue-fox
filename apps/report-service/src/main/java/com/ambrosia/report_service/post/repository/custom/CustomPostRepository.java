package com.ambrosia.report_service.post.repository.custom;

import com.ambrosia.content_service.kafka_events.PostEvent;

import java.util.List;

public interface CustomPostRepository {
    void process(List<PostEvent> toUpsert, List<PostEvent> toDelete);
}
