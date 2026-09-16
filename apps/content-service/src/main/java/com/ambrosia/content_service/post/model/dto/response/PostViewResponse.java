package com.ambrosia.content_service.post.model.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public record PostViewResponse(
    long id,
    UUID authorId,
    String title,
    String preview,

    @JsonInclude(value = Include.NON_NULL)
    List<String> tags,

    int likeCount,
    int commentCount,
    long viewCount,

    Instant publishedAt,

    boolean isLiked,

    PostResponse response,
    CommunityResponse community 
) {}
