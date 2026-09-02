package com.ambrosia.community_service.utils;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import com.ambrosia.community_service.community.model.entity.Community;
import com.ambrosia.community_service.community.repository.CommunityRepository;

@TestComponent
public class CommunityCreator {
    @Autowired CommunityRepository communityRepository;

    public Community createCommunity(){
        return communityRepository.save(
            Factory.createCommunity()
        );
    }

    public Community createCommunity(String name, UUID ownerId){
        return communityRepository.save(
            Factory.createCommunity(name, ownerId)
        );
    }

    public void cleanUp(){
        communityRepository.deleteAll();
    }
}
