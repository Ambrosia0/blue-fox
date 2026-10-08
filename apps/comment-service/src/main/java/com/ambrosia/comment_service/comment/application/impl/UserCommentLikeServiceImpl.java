package com.ambrosia.comment_service.comment.application.impl;

import org.springframework.stereotype.Service;

import com.ambrosia.comment_service.comment.application.UserCommentLikeService;
import com.ambrosia.comment_service.comment.domain.policy.CommentLikePolicy;
import com.ambrosia.comment_service.like.application.LikeAggregationService;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicyContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserCommentLikeServiceImpl implements UserCommentLikeService {
    private final LikeAggregationService likeAggregationService;

    private final GenericPolicyContext genericPolicyContext;

    @Override
    public void likeComment(long commentId, Actor actor) {
        genericPolicyContext.evaluate(actor, CommentLikePolicy.class, commentId);
        likeAggregationService.add(commentId, actor.id(), true);
    }

    @Override
    public void unlikeComment(long commentId, Actor actor) {
        genericPolicyContext.evaluate(actor, CommentLikePolicy.class, commentId);
        likeAggregationService.add(commentId, actor.id(), false);
    }
}
