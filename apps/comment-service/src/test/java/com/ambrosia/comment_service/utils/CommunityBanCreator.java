package com.ambrosia.comment_service.utils;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import com.ambrosia.comment_service.community.model.entity.CommunityBanProjection;
import com.ambrosia.comment_service.community.model.entity.key.CommunityBanKey;
import com.ambrosia.comment_service.community.repository.CommunityBanRepository;

@TestComponent
public class CommunityBanCreator {
    @Autowired
    private CommunityBanRepository communityBanRepository;

    public CommunityBanProjection create(long communityId){
        return communityBanRepository.save(
            new CommunityBanProjection(
                new CommunityBanKey(
                    communityId,
                    UUID.randomUUID()
                ),
                true
            )
        );
    }

    public CommunityBanProjection create(long communityId, UUID userId){
        return communityBanRepository.save(
            new CommunityBanProjection(
                new CommunityBanKey(
                    communityId,
                    userId
                ),
                true
            )
        );
    }
}
