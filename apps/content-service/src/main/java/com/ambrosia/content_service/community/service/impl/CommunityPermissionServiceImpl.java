package com.ambrosia.content_service.community.service.impl;

import org.springframework.stereotype.Service;

import com.ambrosia.content_service.community.model.dto.CommunityUserData;
import com.ambrosia.content_service.community.repository.CommunityQueryRepository;
import com.ambrosia.content_service.community.service.PostPermissionService;
import com.ambrosia.content_service.exception.api.CommunityDoesntExistException;
import com.ambrosia.content_service.exception.api.PrivateReplyException;
import com.ambrosia.content_service.post.exception.PostDoesntExistException;
import com.ambrosia.content_service.post.utils.policy.PostPolicy;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommunityPermissionServiceImpl implements PostPermissionService{
    private final CommunityQueryRepository communityQueryRepository;

    @Override
    public CommunityUserData validatePostInCommunity(PostPolicy policy, long communityId) {
        var userData = communityQueryRepository.findCommunityUserDataByCommunityId(communityId, policy.userId())
            .orElseThrow(() -> new CommunityDoesntExistException());
        policy.validateCreate(userData);
        return userData;
    }
    
    @Override
    public @Nullable CommunityUserData validatePostToReply(
            PostPolicy policy, 
            long postId, 
            @Nullable Long communityId) {
        
        var communityData = communityQueryRepository.findCommunityUserDataByReplyId(
                policy.userId(),  
                communityId,
                postId
            );
        if(communityData.isEmpty()){
            if(communityId != null) // community must be present
                throw new CommunityDoesntExistException();
            else
                return null; // there is no related community to post
        }
        
        var c1 = communityData.getFirst();
        var c2 = communityData.getLast();

        // reply can be only inside private community
        if(communityData.size() == 1 && c1.isCommunityPrivate())
            throw new PrivateReplyException();

        // can't reply outside private community
        if(!c1.communityId().equals(c2.communityId()) && 
                (c1.isCommunityPrivate() || c2.isCommunityPrivate()))
            throw new PrivateReplyException();

        CommunityUserData toReturn = null;
        
        // posting to community
        if(c1.communityId().equals(communityId)){
            policy.validateCreate(c1);
            policy.validateReply(c2);
            toReturn = c1;
        }else if(communityId == null){ // only checking reply policy if not posting to community
            policy.validateReply(c1);
            toReturn = c2;
        }
        return toReturn;
    }

    @Override
    public void validateViewCommunity(PostPolicy policy, long communityId) {
        var userData = communityQueryRepository.findCommunityUserDataByCommunityId(communityId, policy.userId())
            .orElseThrow(() -> new CommunityDoesntExistException());
        policy.validateView(userData);
    }

    @Override
    public void validatePostDelete(PostPolicy policy, long postId) {
        var userData = communityQueryRepository.findPostUserDataByPostId(postId, policy.userId());
        if(userData.isEmpty())
            throw new PostDoesntExistException();
        policy.validateDelete(userData.get());
    }
}
