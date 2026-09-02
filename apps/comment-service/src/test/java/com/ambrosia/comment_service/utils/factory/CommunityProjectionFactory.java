package com.ambrosia.comment_service.utils.factory;

import java.util.concurrent.ThreadLocalRandom;

import com.ambrosia.comment_service.community.model.entity.CommunityProjection;

public class CommunityProjectionFactory {
    public static CommunityProjection create(boolean isPrivate){
        return CommunityProjection.builder()
            .id(ThreadLocalRandom.current().nextLong(1L, 999_999_999L))
            .isNew(true)
            .isPrivate(isPrivate)
            .build();
    }
}
