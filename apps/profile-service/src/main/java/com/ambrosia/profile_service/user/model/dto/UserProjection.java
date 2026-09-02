package com.ambrosia.profile_service.user.model.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ambrosia.profile_service.keycloak.dto.UserRepresentation;
import com.ambrosia.profile_service.user.utils.Role;
import com.ambrosia.profile_service.kafka.dto.UserEvent;

public record UserProjection(
    UUID id,
    String username,
    String firstName,
    String lastName,
    String email,
    String avatarId,
    Boolean enabled,
    Role role
) {
    public static UserProjection from(UserEvent userEvent){
        return new UserProjection(
            UUID.fromString(userEvent.userId()),
            userEvent.userDetails().username().toLowerCase(),
            userEvent.userDetails().firstName(),
            userEvent.userDetails().lastName(),
            userEvent.userDetails().email(),
            extractAvatarId(userEvent.userDetails().attributes()),
            userEvent.userDetails().enabled(),
            Role.user
        );
    }
    
    public static UserProjection from(UUID id, UserRepresentation userRepresentation){
        return new UserProjection(
            id,
            userRepresentation.getUsername().toLowerCase(),
            userRepresentation.getFirstName(),
            userRepresentation.getLastName(),
            userRepresentation.getEmail(),
            extractAvatarId(userRepresentation.getAttributes()),
            userRepresentation.getEnabled(),
            Role.from(userRepresentation.getRealmRoles())
        );
    }

    private static String extractAvatarId(Map<String, List<String>> attributes){
        if(attributes != null && attributes.containsKey("avatarId")){
            var res = attributes.get("avatarId");
            return (res.isEmpty() || res.getFirst().isBlank())?
                null:
                res.getFirst();

        }else{
            return null;
        }
    }
}
