package com.ambrosia.content_service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import com.ambrosia.content_service.community.model.entity.CommunityProjection;
import com.ambrosia.content_service.community.repository.CommunityProjectionRepository;

@TestComponent 
public class CommunityCreator {
    @Autowired CommunityProjectionRepository communityProjectionRepository;

    public CommunityProjection createPrivate(){
        return communityProjectionRepository.save(
            CommunityFactory.createPrivate()
        );
    }

    public CommunityProjection createPublic(){
        return communityProjectionRepository.save(
            CommunityFactory.createPublic()
        );
    }
}
