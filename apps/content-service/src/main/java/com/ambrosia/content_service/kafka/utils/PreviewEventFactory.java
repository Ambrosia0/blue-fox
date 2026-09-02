package com.ambrosia.content_service.kafka.utils;

import java.util.Collection;

import com.ambrosia.content_service.kafka_events.PostPreviewEvent;
import com.ambrosia.content_service.post.model.dto.response.PreviewWithScoreResponse;

public class PreviewEventFactory {
    public static PostPreviewEvent from(Collection<PreviewWithScoreResponse> preview){
        return PostPreviewEvent.newBuilder()
            .addAllPostId(preview.stream()
                .map(t -> t.postViewResponse().id())
                .toList()
            )
            .build();
    }
}
