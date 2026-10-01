package com.ambrosia.report_service.post.service;

import com.ambrosia.content_service.kafka_events.PostEvent;

public interface PostProjectionService {
    void process(PostEvent postEvent);
    boolean exist(Long id);
}
