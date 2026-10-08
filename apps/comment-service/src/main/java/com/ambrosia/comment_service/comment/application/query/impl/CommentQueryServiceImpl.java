package com.ambrosia.comment_service.comment.application.query.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.ambrosia.comment_service.comment.api.dto.CommentFilter;
import com.ambrosia.comment_service.comment.api.dto.EventFilter;
import com.ambrosia.comment_service.comment.api.dto.response.CommentData;
import com.ambrosia.comment_service.comment.api.dto.response.ScoredCommentData;
import com.ambrosia.comment_service.comment.application.query.CommentQueryRepository;
import com.ambrosia.comment_service.comment.application.query.CommentQueryService;
import com.ambrosia.comment_service.comment.domain.policy.PostCommentViewPolicy;
import com.ambrosia.comment_service.comment.domain.policy.TreeCommentViewPolicy;
import com.ambrosia.comment_service.exceptions.api.CommentDoesntExistException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicyContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentQueryServiceImpl implements CommentQueryService{
    private final CommentQueryRepository commentQueryRepository;

    private final GenericPolicyContext genericPolicyContext;

    @Override
    public List<ScoredCommentData> getCommentTree(long commentId, Actor actor) {
        genericPolicyContext.evaluate(actor, TreeCommentViewPolicy.class, commentId);
        return commentQueryRepository.getTreeForPostComment(commentId, actor.id());
    }

    @Override
    public List<ScoredCommentData> getCommentsForPost(long postId, EventFilter eventFilter, Actor actor) {
        genericPolicyContext.evaluate(actor, PostCommentViewPolicy.class, postId);
        return commentQueryRepository.getRootCommentsForPost(postId, actor.id(), eventFilter, 20);
    }

    @Override
    public CommentData getComment(long commentId, Actor actor) {
        genericPolicyContext.evaluate(actor, TreeCommentViewPolicy.class, commentId);
        return commentQueryRepository.getComment(commentId, actor.id())
            .orElseThrow(() -> new CommentDoesntExistException());
    }

    @Override
    public Slice<CommentData> getComments(CommentFilter commentFilter, UUID userId, Actor actor, int pageSize) {
        return commentQueryRepository.getComments(commentFilter, userId, actor.id(), pageSize);
    }
}
