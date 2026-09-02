package com.ambrosia.report_service.post.service;

import java.util.UUID;

import com.ambrosia.content_service.kafka_events.PostCreated;
import com.ambrosia.content_service.kafka_events.PostDeleted;

public interface PostProjectionService {
    void create(PostCreated postCreated, UUID eventId);
    void delete(PostDeleted postDeleted, UUID eventId);
    boolean exist(Long id);
}
