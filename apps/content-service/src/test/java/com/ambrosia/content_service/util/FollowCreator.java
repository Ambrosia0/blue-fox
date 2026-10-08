package com.ambrosia.content_service.util;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import com.ambrosia.content_service.community.repository.CommunityFollowProjectionRepository;
import com.ambrosia.content_service.follow.infrastructure.entity.CommunityFollowProjection;

@TestComponent 
public class FollowCreator {
    @Autowired CommunityFollowProjectionRepository communityFollowProjectionRepository;

    public CommunityFollowProjection createFollow(Long communityId, UUID userId){
        return communityFollowProjectionRepository.save(
            CommunityFollowProjection.create(userId, communityId)
        );
    }
}
