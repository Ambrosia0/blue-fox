package com.ambrosia.community_service.utils;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import com.ambrosia.community_service.community.model.entity.ScopeLink;
import com.ambrosia.community_service.community.repository.ScopeLinkRepository;
import com.ambrosia.community_service.community.utils.ScopeEnum;

@TestComponent
public class ScopeLinkCreator {
    @Autowired ScopeLinkRepository scopeLinkRepository;
    @Autowired CommunityCreator communityCreator;

    public Iterable<ScopeLink> createFromScratch(ScopeEnum[] scopes){
        var userId = UUID.randomUUID();
        var community = communityCreator.createCommunity();
        return scopeLinkRepository.saveAll(
            ScopeLink.create(userId, scopes, community.getId())
        );
    }

    public ScopeLink createFromScratch(ScopeEnum scope){
        var userId = UUID.randomUUID();
        var community = communityCreator.createCommunity();
        return scopeLinkRepository.save(
            ScopeLink.create(userId, scope.getId(), community.getId())
        );
    }

    public void cleanUp(){
        scopeLinkRepository.deleteAll();
    }
}