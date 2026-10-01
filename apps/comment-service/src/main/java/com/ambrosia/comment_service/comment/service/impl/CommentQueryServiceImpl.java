package com.ambrosia.comment_service.comment.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.comment_service.comment.model.dto.EventFilter;
import com.ambrosia.comment_service.comment.model.dto.response.CommentData;
import com.ambrosia.comment_service.comment.model.dto.response.ScoredCommentData;
import com.ambrosia.comment_service.comment.repository.CommentQueryRepository;
import com.ambrosia.comment_service.comment.service.CommentQueryService;
import com.ambrosia.comment_service.exceptions.api.CommentDoesntExistException;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentQueryServiceImpl implements CommentQueryService{
    private final CommentQueryRepository commentQueryRepository;

    @Override
    public List<ScoredCommentData> getCommentTree(long commentId, @Nullable UUID requestingUser) {
        return commentQueryRepository.getTreeForPostComment(commentId, requestingUser);
    }

    @Override
    public List<ScoredCommentData> getCommentsForPost(long postId, EventFilter eventFilter, @Nullable UUID requestingUser) {
        return commentQueryRepository.getRootCommentsForPost(postId, requestingUser, eventFilter, 20);
    }

    @Override
    public CommentData getComment(long commentId, @Nullable UUID requestingUser) {
        var data = commentQueryRepository.getComment(commentId, requestingUser)
            .orElseThrow(() -> new CommentDoesntExistException());
        return data;
    }
}
