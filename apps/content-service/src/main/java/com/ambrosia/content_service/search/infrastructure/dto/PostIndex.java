package com.ambrosia.content_service.search.infrastructure.dto;

import com.ambrosia.content_service.post.domain.entity.Post;
import com.ambrosia.content_service.post.infrastructure.entity.CommunityElastic;

import lombok.Builder;

@Builder 
public record PostIndex(
    Post post, 
    CommunityElastic communityElastic
) {}
