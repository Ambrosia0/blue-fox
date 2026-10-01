package com.ambrosia.content_service.util;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import com.ambrosia.content_service.user.model.entity.UserProjection;
import com.ambrosia.content_service.user.repository.UserRepository;

@TestComponent 
public class UserCreator {
    @Autowired UserRepository userRepository;

    public UserProjection create(){
        return userRepository.save(UserProjection.builder()
            .id(UUID.randomUUID())
            .isNew(true)
            .lastName("testname")
            .firstName("testname")
            .username("test"+ThreadLocalRandom.current().nextLong(1L, 999_999_999L))
            .build()
        );
    }

    public void cleanUp(){
        userRepository.deleteAll();
    }
}
