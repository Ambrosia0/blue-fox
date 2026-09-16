package com.ambrosia.content_service.util;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import com.ambrosia.content_service.follow.model.entity.CommunityFollowProjection;
import com.ambrosia.content_service.follow.repository.CommunityFollowProjectionRepository;

@TestComponent 
public class FollowCreator {
    @Autowired CommunityFollowProjectionRepository communityFollowProjectionRepository;

    public CommunityFollowProjection createFollow(Long communityId, UUID userId){
        return communityFollowProjectionRepository.save(
            CommunityFollowProjection.create(userId, communityId)
        );
    }
}
