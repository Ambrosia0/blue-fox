package com.ambrosia.report_service.post.service.mapper;

import org.springframework.stereotype.Component;

import com.ambrosia.content_service.kafka_events.PostCreated;
import com.ambrosia.report_service.post.entity.PostProjection;

@Component 
public class PostProjectionMapper {
    public PostProjection toEntity(Long postId, PostCreated postCreated){
        return new PostProjection(postId, true);
    }
}
