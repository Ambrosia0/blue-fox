package com.ambrosia.community_service.utils;

import java.util.Set;
import java.util.UUID;

import com.ambrosia.community_service.community.model.dto.request.CommunityCreate;
import com.ambrosia.community_service.community.model.entity.Community;

public class Factory {
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
            .tags(Set.of("#tags"))
            .isPrivate(true)
            .build();
    }
}
