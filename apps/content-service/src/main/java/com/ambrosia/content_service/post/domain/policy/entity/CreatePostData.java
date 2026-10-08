package com.ambrosia.content_service.post.domain.policy.entity;

import java.util.Optional;

public record CreatePostData(
    Optional<CreateCommunityData> communityData
){}