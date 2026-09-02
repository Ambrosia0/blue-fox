package com.ambrosia.comment_service.like.model.dto;

import lombok.Builder;

@Builder
public record LikeRecord(
    boolean isIncrement
) {}
