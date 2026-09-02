package com.ambrosia.profile_service.util;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import com.ambrosia.profile_service.user.model.entity.User;
import com.ambrosia.profile_service.user.repository.UserRepository;
import com.ambrosia.profile_service.user.utils.Role;

@TestComponent
public class UserCreator {
    @Autowired UserRepository userRepository;

    public User createFromScratch(){
        return userRepository.save(User.builder()
            .id(UUID.randomUUID())
            .username("TestUsername"+ThreadLocalRandom.current().nextLong())
            .email("testEmail"+ThreadLocalRandom.current().nextLong()+"@test.com")
            .password("testPassword")
            .isNew(true)
            .firstName("firstName")
            .isEnabled(true)
            .lastName("lastName")
            .role(Role.user)
            .build()
        );
    }

    public void cleanUp(){
        userRepository.deleteAll();
    }
}
