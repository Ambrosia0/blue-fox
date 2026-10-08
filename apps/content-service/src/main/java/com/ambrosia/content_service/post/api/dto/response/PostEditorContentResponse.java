package com.ambrosia.content_service.post.api.dto.response;

import java.time.Instant;
import java.util.List;

import com.ambrosia.content_service.post.domain.entity.Post;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;

@Builder
public record PostEditorContentResponse(
    long id,
    String title,
    String content,

    @JsonInclude(value = Include.NON_NULL)
    List<String> tags,
    
    List<String> attachmentIds,

    CommunityResponse communityResponse,

    Instant updatedAt,
    Long version
) {
    public static PostEditorContentResponse from(Post post){
        return new PostEditorContentResponse(
            post.getId(),
            post.getTitle(),
            post.getContent(),
            post.getTags().stream().toList(),
            null,
            null,
            post.getUpdatedAt(),
            post.getVersion()
        );
    }
}
