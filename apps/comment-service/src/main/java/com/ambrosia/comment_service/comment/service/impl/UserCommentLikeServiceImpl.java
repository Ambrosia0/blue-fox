package com.ambrosia.comment_service.comment.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.comment_service.comment.service.UserCommentLikeService;
import com.ambrosia.comment_service.community.service.CommunityPermissionService;
import com.ambrosia.comment_service.like.service.LikeAggregationService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserCommentLikeServiceImpl implements UserCommentLikeService {
    private final LikeAggregationService likeAggregationService;

    private final CommunityPermissionService communityPermissionService;

    @Override
    public void likeComment(long commentId, UUID userId) {
        communityPermissionService.validateCommentLike(userId, commentId);
        likeAggregationService.add(commentId, userId, true);
    }

    @Override
    public void unlikeComment(long commentId, UUID userId) {
        communityPermissionService.validateCommentLike(userId, commentId);
        likeAggregationService.add(commentId, userId, false);
    }
}
