package com.ambrosia.report_service.comment.service.mapper;

import org.springframework.stereotype.Component;

import com.ambrosia.comment_service.kafka_events.CommentCreated;
import com.ambrosia.report_service.comment.entity.CommentProjection;

@Component 
public class CommentProjectionMapper {
    public CommentProjection toEntity(Long commentId, CommentCreated commentCreated){
        return new CommentProjection(commentId, true);
    }
}
