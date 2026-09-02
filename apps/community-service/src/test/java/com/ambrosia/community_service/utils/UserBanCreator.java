package com.ambrosia.community_service.utils;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import com.ambrosia.community_service.community.model.entity.CommunityBan;
import com.ambrosia.community_service.community.repository.CommunityBanRepository;

@TestComponent
public class UserBanCreator {
    @Autowired CommunityCreator communityCreator;
    @Autowired FollowCreator followCreator;
    @Autowired CommunityBanRepository communityBanRepository;

    public CommunityBan createFromScratch(){
        var community = communityCreator.createCommunity();
        var userId = UUID.randomUUID();
        followCreator.create(community.getId(), userId);
        return communityBanRepository.save(CommunityBan.create(
            userId, 
            community.getId(),
            Instant.now().plus(Duration.ofDays(1))
        ));
    }

    public CommunityBan create(Long communityId, UUID userId){
        return communityBanRepository.save(CommunityBan.create(
            userId,
            communityId,
            Instant.now().plus(Duration.ofDays(1))
        ));
    }

    public void cleanUp(){
        communityBanRepository.deleteAll();
    }
}
