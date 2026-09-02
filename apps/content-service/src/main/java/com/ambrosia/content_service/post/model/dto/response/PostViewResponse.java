package com.ambrosia.content_service.post.model.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.relational.core.mapping.Embedded.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public record PostViewResponse(
    long id,
    UUID authorId,
    String title,
    String preview,

    @JsonInclude(value = Include.NON_NULL)
    List<String> tags,

    @JsonInclude(value = Include.NON_NULL)
    Long communityId,

    int likeCount,
    int commentCount,
    long viewCount,

    Instant publishedAt,

    @Nullable
    @JsonInclude(value = Include.NON_NULL)
    Boolean isLiked,

    String communityName,
    UUID communityAvatarId
) {}
