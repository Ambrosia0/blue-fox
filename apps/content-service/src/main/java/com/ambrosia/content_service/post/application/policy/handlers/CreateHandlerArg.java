package com.ambrosia.content_service.post.application.policy.handlers;

import jakarta.annotation.Nullable;

public record CreateHandlerArg(
    Long replyingPostId,
    Long communityId
) {
    public static CreateHandlerArg create(@Nullable Long replyingPostId, @Nullable Long communityId){
        return new CreateHandlerArg(replyingPostId, communityId);
    }
}
