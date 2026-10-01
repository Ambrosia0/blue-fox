package com.ambrosia.community_service.utils;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.ambrosia.community_service.user.model.entity.UserProjection;

public class UserFactory {
    public static UserProjection create(){
        return UserProjection.builder()
            .id(UUID.randomUUID())
            .isNew(true)
            .lastName("testname")
            .firstName("testname")
            .username("test"+ThreadLocalRandom.current().nextLong(1L, 999_999_999L))
            .build();
    }
}
