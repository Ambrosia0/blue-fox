package com.ambrosia.community_service.utils;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import com.ambrosia.community_service.follow.model.entity.CommunityFollow;
import com.ambrosia.community_service.follow.repository.CommunityFollowRepository;

@TestComponent
public class FollowCreator {
    @Autowired CommunityCreator communityCreator;
    @Autowired CommunityFollowRepository communityFollowRepository;

    public CommunityFollow createFromScratch(){
        var community = communityCreator.createCommunity();
        return communityFollowRepository.save(
            CommunityFollow.create(
                UUID.randomUUID(),
                community.getId()
            )
        );
    }

    public CommunityFollow create(Long communityId, UUID userId){
        return communityFollowRepository.save(
            CommunityFollow.create(
                userId,
                communityId
            )
        );
    }

    public void cleanUp(){
        communityFollowRepository.deleteAll();
    }
}
