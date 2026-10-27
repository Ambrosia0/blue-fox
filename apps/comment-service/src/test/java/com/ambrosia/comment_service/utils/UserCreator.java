package com.ambrosia.comment_service.utils;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.ambrosia.comment_service.user.infrastructure.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import com.ambrosia.comment_service.user.model.entity.UserProjection;

@TestComponent 
public class UserCreator {
    @Autowired
    UserRepository userRepository;

    public UserProjection create(){
        return userRepository.save(
            UserProjection.builder()
                .id(UUID.randomUUID())
                .isNew(true)
                .lastName("testname")
                .firstName("testname")
                .version(0L)
                .username("test"+ThreadLocalRandom.current().nextLong(1L, 999_999_999L))
                .build()
        );
    }

    public void cleanUp(){
        
    }
}
