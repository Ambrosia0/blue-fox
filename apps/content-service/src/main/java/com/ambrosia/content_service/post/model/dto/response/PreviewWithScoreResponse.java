package com.ambrosia.content_service.post.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonRootName("post")
public record PreviewWithScoreResponse(
    @JsonUnwrapped
    PostViewResponse postViewResponse,

    @JsonInclude(value = Include.NON_NULL)
    Float score
){}
