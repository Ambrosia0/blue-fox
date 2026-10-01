package com.ambrosia.comment_service.comment.service.impl;

import org.springframework.stereotype.Service;

import com.ambrosia.comment_service.comment.service.UserCommentLikeService;
import com.ambrosia.comment_service.community.service.CommentPermissionService;
import com.ambrosia.comment_service.community.utils.CommentPolicy;
import com.ambrosia.comment_service.like.service.LikeAggregationService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserCommentLikeServiceImpl implements UserCommentLikeService {
    private final LikeAggregationService likeAggregationService;

    private final CommentPermissionService communityPermissionService;

    @Override
    public void likeComment(long commentId, CommentPolicy commentPolicy) {
        communityPermissionService.validateCommentLike(commentPolicy, commentId);
        likeAggregationService.add(commentId, commentPolicy.id(), true);
    }

    @Override
    public void unlikeComment(long commentId, CommentPolicy commentPolicy) {
        communityPermissionService.validateCommentLike(commentPolicy, commentId);
        likeAggregationService.add(commentId, commentPolicy.id(), false);
    }
}
