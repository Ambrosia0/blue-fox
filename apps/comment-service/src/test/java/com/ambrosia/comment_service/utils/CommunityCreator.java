package com.ambrosia.comment_service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import com.ambrosia.comment_service.community.model.entity.CommunityProjection;
import com.ambrosia.comment_service.community.repository.CommunityProjectionRepository;
import com.ambrosia.comment_service.utils.factory.CommunityProjectionFactory;

@TestComponent
public class CommunityCreator {
    @Autowired CommunityProjectionRepository communityProjectionRepository;

    public CommunityProjection createFromScratch(boolean isPrivate){
        return communityProjectionRepository.save(
            CommunityProjectionFactory.create(isPrivate)
        );
    }
}
