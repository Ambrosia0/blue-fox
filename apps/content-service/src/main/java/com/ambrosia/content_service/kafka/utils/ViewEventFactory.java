package com.ambrosia.content_service.kafka.utils;

import com.ambrosia.content_service.kafka_events.PostViewEvent;
import com.ambrosia.content_service.post.model.dto.response.PostContentResponse;

public class ViewEventFactory {
    
    public static PostViewEvent from(PostContentResponse view){
        return PostViewEvent.newBuilder()
            .setPostId(view.getId())
            .build();
    }
}
