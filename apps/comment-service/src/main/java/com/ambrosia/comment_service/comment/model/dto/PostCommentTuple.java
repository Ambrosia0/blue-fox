package com.ambrosia.comment_service.comment.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PostCommentTuple(
    @JsonProperty("postId")
    long id,

    int commentCount
) {}
