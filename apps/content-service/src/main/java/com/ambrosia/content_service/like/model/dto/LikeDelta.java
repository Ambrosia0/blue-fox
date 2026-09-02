package com.ambrosia.content_service.like.model.dto;

public record LikeDelta(
    long postId,
    long delta
) {}
