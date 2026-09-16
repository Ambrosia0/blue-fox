package com.ambrosia.content_service.search.model.dto;

import com.ambrosia.content_service.community.model.dto.CommunityUserData;
import com.ambrosia.content_service.post.model.entity.Post;

import lombok.Builder;

@Builder 
public record PostIndex(
    Post post, 
    CommunityUserData communityUserData
) {}
