package com.ambrosia.comment_service.comment.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.comment_service.comment.model.dto.EventFilter;
import com.ambrosia.comment_service.comment.model.dto.response.RootCommentData;
import com.ambrosia.comment_service.comment.model.dto.response.TreeCommentData;
import com.ambrosia.comment_service.comment.repository.CommentQueryRepository;
import com.ambrosia.comment_service.comment.service.CommentQueryService;
import com.ambrosia.comment_service.community.service.CommunityPermissionService;
import com.ambrosia.comment_service.exceptions.api.CommentDoesntExistException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentQueryServiceImpl implements CommentQueryService{
    private final CommentQueryRepository commentQueryRepository;

    private final CommunityPermissionService communityPermissionService;

    @Override
    public List<TreeCommentData> getCommentTree(long commentId, UUID userId) {
        communityPermissionService.validateCommentTreeView(userId, commentId);
        return userId != null? 
            commentQueryRepository.getTreeForPostCommentWithLike(commentId, userId):
            commentQueryRepository.getTreeForPostComment(commentId);
    }

    @Override
    public List<RootCommentData> getCommentsForPost(long postId, EventFilter eventFilter, UUID userId) {
        communityPermissionService.validateCommentView(userId, postId);
        return userId != null?
            commentQueryRepository.getRootCommentsForPostWithLike(postId, userId, eventFilter, 20):
            commentQueryRepository.getRootCommentsForPost(postId, eventFilter, 20);
    }

    @Override
    public TreeCommentData getComment(long commentId, UUID userId) {
        communityPermissionService.validateCommentTreeView(userId, commentId);
        var data = (userId != null?
                commentQueryRepository.getCommentWithLike(commentId, userId):
                commentQueryRepository.getComment(commentId))
            .orElseThrow(() -> new CommentDoesntExistException());
        return data;
    }
}
