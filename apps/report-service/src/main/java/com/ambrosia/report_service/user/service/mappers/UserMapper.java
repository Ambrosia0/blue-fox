package com.ambrosia.report_service.user.service.mappers;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.ambrosia.profile_service.kafka_events.UserCreated;
import com.ambrosia.profile_service.kafka_events.UserUpdated;
import com.ambrosia.report_service.user.entity.UserProjection;

@Component 
public class UserMapper {
    public UserProjection toEntity(UUID userId, UserCreated userCreated){
        return new UserProjection(
            userId, 
            userCreated.getUsername(),
            userCreated.getAvatarId(),
            true
        );
    }

    public UserProjection toEntity(UUID userId, UserUpdated userUpdated){
        return new UserProjection(
            userId,
            userUpdated.getUsername(),
            userUpdated.getAvatarId(),
            false
        );
    }
}
