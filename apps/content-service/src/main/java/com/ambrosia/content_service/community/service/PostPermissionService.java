package com.ambrosia.content_service.community.service;

import com.ambrosia.content_service.community.model.dto.CommunityUserData;
import com.ambrosia.content_service.post.utils.policy.PostPolicy;

import jakarta.annotation.Nullable;

/**
 * Validates permissions in community
 * CommunityPermissionService
 */
public interface PostPermissionService {
    /**
     * Validates possibility to post in community
     * @param policy policy
     * @param communityId
     * @return community data in which post is creating
     */
    CommunityUserData validatePostInCommunity(PostPolicy policy, long communityId);

    /**
     * Validates possibility to reply to possibly community post in community
     * @param policy policy
     * @param postId replying post
     * @param communityId community where post is creating
     * @return community data in which post is creating
     */
    @Nullable CommunityUserData validatePostToReply(
        PostPolicy policy, 
        long postId,
        @Nullable Long communityId
    );

    /**
     * Validates possibility to view community posts
     * @param policy policy
     * @param communityId viwed community
     */
    void validateViewCommunity(
        PostPolicy policy,
        long communityId
    );

    /**
     * Validates possibility to delete post
     * @param policy policy
     * @param postId deleted post
     */
    void validatePostDelete(
        PostPolicy policy,
        long postId
    );
}
