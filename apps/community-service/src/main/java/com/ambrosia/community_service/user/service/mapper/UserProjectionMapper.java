package com.ambrosia.community_service.user.service.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.ambrosia.community_service.user.model.entity.UserProjection;
import com.ambrosia.profile_service.kafka_events.UserCreated;
import com.ambrosia.profile_service.kafka_events.UserUpdated;

@Component 
public class UserProjectionMapper {
    public UserProjection toEntity(UUID id, UserCreated userCreated){
        return UserProjection.builder()
            .id(id)
            .firstName(userCreated.getFirstName())
            .lastName(userCreated.getLastName())
            .username(userCreated.getUsername())
            .isNew(true)
            .avatarId(userCreated.getAvatarId())
            .build();
    }

    public UserProjection toEntity(UUID id, UserUpdated userUpdated){
        return UserProjection.builder()
            .id(id)
            .firstName(userUpdated.getFirstName())
            .lastName(userUpdated.getLastName())
            .username(userUpdated.getUsername())
            .isNew(false)
            .avatarId(userUpdated.getAvatarId())
            .build();
    }
}
