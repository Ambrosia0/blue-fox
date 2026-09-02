package com.ambrosia.comment_service.post.service;

import java.util.List;

import com.ambrosia.content_service.kafka_events.PostEvent;

public interface PostProjectionCreator {
    void process(List<PostEvent> toInsert, List<PostEvent> toDelete);
}
