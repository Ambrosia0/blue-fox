package com.ambrosia.comment_service.comment.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.comment_service.comment.repository.CommentRepository;
import com.ambrosia.comment_service.comment.service.ModeratorCommentService;
import com.ambrosia.comment_service.exceptions.api.CommentDoesntRelatedException;
import com.ambrosia.comment_service.exceptions.api.NotEnoughPermissionsException;
import com.ambrosia.comment_service.grpc.CommunityService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ModeratorCommentServiceImpl implements ModeratorCommentService{
    private final CommunityService communityService;
    
    private final CommentRepository commentRepository;

    @Override
    public void deleteComment(UUID requestingUser, long commentId) {
        var communityId = commentRepository.findRelatedProjectionCommunityId(commentId)
            .orElseThrow(() -> new CommentDoesntRelatedException());
        var allowed = communityService.isUserAllowed(requestingUser, "COMMENT_DELETE", communityId);
        if(!allowed)
            throw new NotEnoughPermissionsException();
        commentRepository.hideCommentById(commentId);
    }
}
