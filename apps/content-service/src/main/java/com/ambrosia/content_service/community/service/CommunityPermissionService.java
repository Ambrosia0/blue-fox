package com.ambrosia.content_service.community.service;

import java.util.UUID;

import com.ambrosia.content_service.community.model.dto.CommunityUserData;
import com.ambrosia.content_service.post.utils.policy.PostPolicy;

import jakarta.annotation.Nullable;

/**
 * Validates permissions in community
 * CommunityPermissionService
 */
public interface CommunityPermissionService {
    /**
     * Validates possibility to post in community
     * @param userId
     * @param communityId
     * @return community data in which post is creating
     */
    CommunityUserData validatePostInCommunity(UUID userId, PostPolicy policy, long communityId);

    /**
     * Validates possibility to reply to possibly community post in community
     * @param userId user who creates post
     * @param postId replying post
     * @param communityId community where post is creating
     * @return community data in which post is creating
     */
    @Nullable CommunityUserData validatePostToReply(
        UUID userId, 
        PostPolicy policy, 
        long postId, 
        @Nullable Long communityId
    );
}
