package com.ambrosia.comment_service.utils.factory;

import java.util.concurrent.ThreadLocalRandom;

import com.ambrosia.comment_service.post.model.entity.PostProjection;

public class PostProjectionFactory {
    public static PostProjection createProjection(Long communityId){
        return PostProjection.builder()
            .communityId(communityId)
            .id(ThreadLocalRandom.current().nextLong(1L, 999_999_999L))
            .isNew(true)
            .build();
    }
    public static PostProjection createProjection(){
        return PostProjection.builder()
            .id(ThreadLocalRandom.current().nextLong(1L, 999_999_999L))
            .isNew(true)
            .build();
    }
}
