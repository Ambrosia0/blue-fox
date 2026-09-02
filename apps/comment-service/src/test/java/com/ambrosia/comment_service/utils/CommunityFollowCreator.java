package com.ambrosia.comment_service.utils;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import com.ambrosia.comment_service.community.model.entity.CommunityFollowProjection;
import com.ambrosia.comment_service.community.model.entity.key.CommunityFollowKey;
import com.ambrosia.comment_service.community.repository.CommunityFollowProjectionRepository;

@TestComponent
public class CommunityFollowCreator {
    @Autowired CommunityFollowProjectionRepository communityFollowProjectionRepository;

    public CommunityFollowProjection create(long communityId, UUID userId){
        return communityFollowProjectionRepository.save(
            new CommunityFollowProjection(
                new CommunityFollowKey(userId, communityId),
                true
            )
        );
    }
}
