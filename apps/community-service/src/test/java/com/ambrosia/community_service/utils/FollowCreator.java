package com.ambrosia.community_service.utils;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;

import com.ambrosia.community_service.follow.model.entity.CommunityFollow;
import com.ambrosia.community_service.follow.repository.CommunityFollowRepository;

@TestComponent
@Import({CommunityCreator.class})
public class FollowCreator {
    @Autowired CommunityCreator communityCreator;
    @Autowired UserCreator userCreator;
    @Autowired CommunityFollowRepository communityFollowRepository;

    public CommunityFollow createFromScratch(boolean isCommunityPrivate){
        var community = communityCreator.createCommunity(isCommunityPrivate);
        var user = userCreator.create();
        return communityFollowRepository.save(
            CommunityFollow.create(
                user.getId(),
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
