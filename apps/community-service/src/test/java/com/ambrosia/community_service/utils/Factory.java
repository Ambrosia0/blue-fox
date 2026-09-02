package com.ambrosia.community_service.utils;

import java.util.List;
import java.util.UUID;

import com.ambrosia.community_service.community.model.dto.request.CommunityCreate;
import com.ambrosia.community_service.community.model.entity.Community;

public class Factory {
    public static Community createCommunity(){
        return Community.builder()
            .avatarId(null)
            .displayedName("TestCommunity")
            .slug("TestCommunity")
            .ownerId(UUID.randomUUID())
            .tags(List.of("#tags"))
            .build();
    }

    public static Community createCommunity(String name, UUID ownedId){
        return Community.builder()
            .avatarId(null)
            .displayedName(name)
            .slug(name)
            .ownerId(ownedId)
            .tags(List.of("#tags"))
            .build();
    }

    public static CommunityCreate createRequest(String name, String slug, boolean isPrivate){
        return new CommunityCreate(
            name,
            slug,
            isPrivate, 
            null
        );
    }

    public static Community createPrivateCommunity(){
        return Community.builder()
            .avatarId(null)
            .displayedName("TestCommunity")
            .slug("test_community")
            .ownerId(UUID.randomUUID())
            .tags(List.of("#tags"))
            .isPrivate(true)
            .build();
    }
}
