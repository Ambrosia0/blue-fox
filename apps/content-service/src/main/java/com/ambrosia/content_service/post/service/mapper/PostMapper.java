package com.ambrosia.content_service.post.service.mapper;

import org.springframework.stereotype.Component;

import com.ambrosia.content_service.post.model.dto.response.PostEditorViewResponse;
import com.ambrosia.content_service.post.model.entity.Post;

@Component
public class PostMapper {
    public PostEditorViewResponse toDto(Post post){
        return new PostEditorViewResponse(
                    post.getId(),
                    post.getAuthorId(),
                    post.getTitle(), 
                    post.getCreatedAt()
        );
    }
}
