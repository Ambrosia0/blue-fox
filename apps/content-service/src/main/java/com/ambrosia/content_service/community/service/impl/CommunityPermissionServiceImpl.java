package com.ambrosia.content_service.community.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.content_service.community.model.dto.CommunityUserData;
import com.ambrosia.content_service.community.repository.CommunityQueryRepository;
import com.ambrosia.content_service.community.service.CommunityPermissionService;
import com.ambrosia.content_service.exception.api.CommunityDoesntExistException;
import com.ambrosia.content_service.exception.api.PrivateReplyException;
import com.ambrosia.content_service.post.utils.policy.PostPolicy;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommunityPermissionServiceImpl implements CommunityPermissionService{
    private final CommunityQueryRepository communityQueryRepository;

    @Override
    public CommunityUserData validatePostInCommunity(UUID userId, PostPolicy policy, long communityId) {
        var userData = communityQueryRepository.findCommunityUserDataByCommunityId(communityId, userId)
            .orElseThrow(() -> new CommunityDoesntExistException());
        policy.validateCreate(userData);
        return userData;
    }
    
    @Override
    public CommunityUserData validatePostToReply(
            UUID userId, 
            PostPolicy policy, 
            long postId, 
            @Nullable Long communityId) {
        var communityData = communityQueryRepository.findCommunityUserDataByReplyId(
                userId,  
                communityId,
                postId
            );
        if(communityData.isEmpty())
            throw new CommunityDoesntExistException();
        
        var p1 = communityData.getFirst();
        var p2 = communityData.getLast();

        if(communityData.size() == 1 && p1.isCommunityPrivate())
            throw new PrivateReplyException();

        if((p1.communityId() != p2.communityId()) && 
                (p1.isCommunityPrivate() || p2.isCommunityPrivate()))
            throw new PrivateReplyException();

        policy.validateCreate(p1);
        policy.validateCreate(p2);
        
        return p1.communityId() == communityId? 
            p1:
            p2;
    }
}
