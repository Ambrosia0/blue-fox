package com.ambrosia.comment_service.comment.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public record ScoredCommentData(
    CommentData commentData,

    @JsonInclude(value = Include.NON_NULL)
    Float score
) {}
