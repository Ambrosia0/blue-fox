package com.ambrosia.comment_service.community.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ambrosia.comment_service.community.repository.CommunityQueryRepository;
import com.ambrosia.comment_service.community.service.CommentPermissionService;
import com.ambrosia.comment_service.community.utils.CommentPolicy;
import com.ambrosia.comment_service.exceptions.api.CommentDoesntExistException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommunityPermissionServiceImpl implements CommentPermissionService{
    private final CommunityQueryRepository communityQueryRepository;
    
    @Override
    public void validateCommentCreate(CommentPolicy commentPolicy, long postId) {
        Assert.notNull(commentPolicy.id(), "userId must not be null!");
        var communityUserDataOpt = communityQueryRepository.findCommunityUserDataByPostId(postId, commentPolicy.id());
        if(communityUserDataOpt.isEmpty()) // community to check doesnt exist
            return;
        commentPolicy.validateCommentCreate(communityUserDataOpt.get());
    }

    @Override
    public void validateCommentLike(CommentPolicy commentPolicy, long commentId) {
        Assert.notNull(commentPolicy.id(), "userId must not be null!");
        var dataOpt = communityQueryRepository.findCommunityUserDataByCommentId(commentId, commentPolicy.id());
        if(dataOpt.isEmpty())
            return;
        commentPolicy.validateCommentLike(dataOpt.get());
    }

    @Override
    public void validateCommentView(CommentPolicy commentPolicy, long postId) {
        var communityUserDataOpt = communityQueryRepository.findCommunityUserDataByPostId(postId, commentPolicy.id());
        if(communityUserDataOpt.isEmpty())
            return;
        commentPolicy.validateCommentView(communityUserDataOpt.get());
    }

    @Override
    public void validateCommentDelete(CommentPolicy commentPolicy, long commentId) {
        Assert.notNull(commentPolicy.id(), "userId must not be null!");
        var userDataOpt = communityQueryRepository.findCommentUserDataByCommentId(commentId, commentPolicy.id());
        if(userDataOpt.isEmpty())
            throw new CommentDoesntExistException();
        commentPolicy.validateDelete(userDataOpt.get());
    }
}