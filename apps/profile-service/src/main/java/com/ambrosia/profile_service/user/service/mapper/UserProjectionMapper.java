package com.ambrosia.profile_service.user.service.mapper;

import org.springframework.stereotype.Component;

import com.ambrosia.profile_service.user.model.dto.UserProjection;
import com.ambrosia.profile_service.user.model.entity.User;
import com.ambrosia.profile_service.user.model.entity.UserSettings;

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
            .isNew(true)
            .isActive(true)
            .isEnabled(userProjection.enabled())
            .userSettings(UserSettings.builder()
                .id(userProjection.id())
                .build()
            )
            .build();
    }

    public User apply(User user, UserProjection userProjection){
        user.setEnabled(userProjection.enabled());
        user.setEmail(userProjection.email());
        user.setAvatarId(userProjection.avatarId());
        user.setUsername(userProjection.username());
        user.setFirstName(userProjection.firstName());
        user.setLastName(userProjection.lastName());
        if(userProjection.role() != null) 
            user.setRole(userProjection.role());
        return user;
    }
}
