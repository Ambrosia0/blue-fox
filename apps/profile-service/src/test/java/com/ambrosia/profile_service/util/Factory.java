package com.ambrosia.profile_service.util;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.ambrosia.profile_service.user.model.entity.User;
import com.ambrosia.profile_service.user.utils.Role;

public class Factory {
    public static User createUser(){
        return User.builder()
            .id(UUID.randomUUID())
            .username("TestUsername"+ThreadLocalRandom.current().nextLong())
            .email("testEmail"+ThreadLocalRandom.current().nextLong()+"@test.com")
            .password("testPassword")
            .isNew(true)
            .firstName("firstName")
            .isEnabled(true)
            .lastName("lastName")
            .role(Role.user)
            .build();
    }

    public static User createUser(UUID id){
        return User.builder()
            .id(id)
            .username("TestUsername"+ThreadLocalRandom.current().nextLong())
            .email("testEmail"+ThreadLocalRandom.current().nextLong()+"@test.com")
            .password("testPassword")
            .isEnabled(true)
            .isNew(true)
            .role(Role.user)
            .build();
    }
}
