package com.ambrosia.content_service.post.utils.policy;

import java.util.UUID;

import com.ambrosia.content_service.community.model.dto.CommunityUserData;
import com.ambrosia.content_service.community.model.dto.PostUserData;

import jakarta.annotation.Nullable;

public interface PostPolicy {
    void validateCreate(CommunityUserData userData);
    void validateReply(CommunityUserData userData);
    void validateView(CommunityUserData userData);
    void validateDelete(PostUserData userData);
    @Nullable UUID userId();
}
