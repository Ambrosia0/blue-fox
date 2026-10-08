package com.ambrosia.comment_service.like.infrastructure.dto;

import lombok.Builder;

@Builder
public record LikeRecord(
    boolean isIncrement
) {}
