package com.ambrosia.community_service.utils;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;

import com.ambrosia.community_service.follow.model.entity.CommunityFollowRequest;
import com.ambrosia.community_service.follow.repository.CommunityFollowRequestRepository;

@TestComponent 
@Import({CommunityCreator.class, UserCreator.class})
public class CommunityFollowRequestCreator {
    @Autowired CommunityCreator communityCreator;

    @Autowired CommunityFollowRequestRepository communityFollowRequestRepository;

    @Autowired UserCreator userCreator;

    public CommunityFollowRequest create(){
        var community = communityCreator.createCommunity(true);
        var user = userCreator.create();
        return communityFollowRequestRepository.save(
            CommunityFollowRequest.create(user.getId(), community.getId())
        );
    }

    public CommunityFollowRequest create(UUID userId, Long communityId){
        return communityFollowRequestRepository.save(
            CommunityFollowRequest.create(userId, communityId)
        );
    }

    public CommunityFollowRequest create(Long communityId){
        var user = userCreator.create();
        return communityFollowRequestRepository.save(
            CommunityFollowRequest.create(user.getId(), communityId)
        );
    }

    public void cleanUp(){
        communityFollowRequestRepository.deleteAll();
    }
}
