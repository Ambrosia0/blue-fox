package com.ambrosia.profile_service.infrastructure.kafka.mapper;

import org.springframework.stereotype.Component;

import com.ambrosia.profile_service.user.api.dto.UserProjection;
import com.ambrosia.profile_service.user.domain.entity.User;

@Component 
public class UserProjectionMapper {
    public User toEntity(UserProjection userProjection){
        return User.builder()
            .id(userProjection.id())
            .username(userProjection.username())
            .firstName(userProjection.firstName())
            .email(userProjection.email())
            .lastName(userProjection.lastName())
            .avatarId(userProjection.avatarId())
            .role(userProjection.role())
            .isEnabled(userProjection.enabled())
            .build();
    }
}
