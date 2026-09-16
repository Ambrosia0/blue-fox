package com.ambrosia.content_service.util;

import com.ambrosia.content_service.community.model.entity.CommunityProjection;

public class CommunityFactory {
    public static CommunityProjection createPrivate(){
        var id = TestUtils.randLong();
        return CommunityProjection.builder()
            .id(id)
            .name("TestCommunity"+id)
            .isNew(true)
            .isPrivate(true)
            .build();
    }

    public static CommunityProjection createPublic(){
        var id = TestUtils.randLong();
        return CommunityProjection.builder()
            .id(id)
            .name("TestCommunity"+id)
            .isNew(true)
            .isPrivate(false)
            .build();
    }
}
