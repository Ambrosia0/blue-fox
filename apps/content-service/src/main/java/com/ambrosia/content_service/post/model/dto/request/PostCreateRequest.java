package com.ambrosia.content_service.post.model.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder 
public record PostCreateRequest(
    @Size(min = 6, max = 100)
    String title,

    Long replyId,

    Long communityId
) {}
