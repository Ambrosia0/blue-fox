package com.ambrosia.collaboration_service.utils.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.ambrosia.collaboration_service.model.entity.User;

@Component 
public class UserMapper {
    public User toUser(com.ambrosia.content_service.grpc.User grpcUser){
        return new User(
            UUID.fromString(grpcUser.getId()), 
            grpcUser.getUsername(),
            grpcUser.getFirstName(), 
            grpcUser.getLastName(),
            grpcUser.getAvatarId()
        );
    }
}
