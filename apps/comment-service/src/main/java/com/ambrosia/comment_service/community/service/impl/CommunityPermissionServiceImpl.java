package com.ambrosia.comment_service.community.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ambrosia.comment_service.community.repository.CommunityQueryRepository;
import com.ambrosia.comment_service.community.service.CommunityPermissionService;
import com.ambrosia.comment_service.exceptions.api.DoesntFollowedOnPrivateCommunityException;
import com.ambrosia.comment_service.exceptions.api.UserBannedException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommunityPermissionServiceImpl implements CommunityPermissionService{
    private final CommunityQueryRepository communityQueryRepository;
    
    @Override
    public void validateCommentCreate(UUID userId, long postId) {
        Assert.notNull(userId, "userId must not be null!");
        var communityUserDataOpt = communityQueryRepository.findCommunityUserDataByPostId(postId, userId);
        if(communityUserDataOpt.isEmpty())
            return;
        var communityUserData = communityUserDataOpt.get();
        if(communityUserData.isBanned())
            throw new UserBannedException();
        if(communityUserData.isCommunityPrivate() && !communityUserData.isFollowed())
            throw new DoesntFollowedOnPrivateCommunityException();
    }

    @Override
    public void validateCommentLike(UUID userId, long commentId) {
        Assert.notNull(userId, "userId must not be null!");
        validateCommentTreeView(userId, commentId);
    }

    @Override
    public void validateCommentTreeView(UUID userId, long commentId) {
        var communityUserDataOpt = communityQueryRepository.findCommunityUserDataByCommentId(commentId, userId);
        if(communityUserDataOpt.isEmpty())
            return;
        var communityUserData = communityUserDataOpt.get();
        if(communityUserData.isCommunityPrivate() && (!communityUserData.isFollowed() || communityUserData.isBanned()))
            throw new DoesntFollowedOnPrivateCommunityException();
    }

    @Override
    public void validateCommentView(UUID userId, long postId) {
        var communityUserDataOpt = communityQueryRepository.findCommunityUserDataByPostId(postId, userId);
        if(communityUserDataOpt.isEmpty())
            return;
        var communityUserData = communityUserDataOpt.get();
        if(communityUserData.isCommunityPrivate() && (!communityUserData.isFollowed() || communityUserData.isBanned()))
            throw new DoesntFollowedOnPrivateCommunityException();
    }
}