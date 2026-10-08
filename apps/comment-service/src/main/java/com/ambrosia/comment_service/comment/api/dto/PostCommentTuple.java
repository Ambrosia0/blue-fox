package com.ambrosia.comment_service.comment.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PostCommentTuple(
    @JsonProperty("postId")
    long id,

    int commentCount
) {}
