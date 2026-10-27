package com.ambrosia.report_service.user.infrastructure;

import com.ambrosia.profile_service.kafka_events.UserCreated;
import com.ambrosia.profile_service.kafka_events.UserDeleted;
import com.ambrosia.profile_service.kafka_events.UserEvent;
import com.ambrosia.report_service.BaseIntegrationTest;
import com.ambrosia.report_service.user.repository.UserProjectionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

public class UserProjectionRepositoryTest extends BaseIntegrationTest {
    @Autowired
    UserProjectionRepository userRepository;
    
    @Test
    void shouldCreateUserThenDeleteUser(){
        var createEvent = createEvent();
        userRepository.process(
            List.of(createEvent),
            List.of()
        );
        var userId = UUID.fromString(createEvent.getUserId());
        var user = userRepository.findById(userId);
        Assertions.assertTrue(user.isPresent());
        
        userRepository.process(
            List.of(),
            List.of(deleteEvent(
                userId,
                createEvent.getVersion()
            ))
        );
        
        user = userRepository.findById(userId);
        Assertions.assertTrue(user.isEmpty());
    }
    
    private UserEvent createEvent(){
        return UserEvent.newBuilder()
            .setUserId(UUID.randomUUID().toString())
            .setVersion(0)
            .setEventId(UUID.randomUUID().toString())
            .setCreated(UserCreated.newBuilder()
                .setUsername("test_user")
                .setFirstName("test")
                .setLastName("test")
                .build()
            )
            .build();
    }
    
    private UserEvent deleteEvent(UUID id, Long version){
        return UserEvent.newBuilder()
            .setUserId(id.toString())
            .setVersion(version)
            .setEventId(UUID.randomUUID().toString())
            .setDeleted(UserDeleted.newBuilder().build())
            .build();
    }
}
