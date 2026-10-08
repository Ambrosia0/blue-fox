package com.ambrosia.content_service.infrastructure.kafka.utils;

import com.ambrosia.content_service.kafka_events.PostViewEvent;
import com.ambrosia.content_service.post.api.dto.response.PostContentResponse;

public class ViewEventFactory {
    
    public static PostViewEvent from(PostContentResponse view){
        return PostViewEvent.newBuilder()
            .setPostId(view.getId())
            .build();
    }
}
