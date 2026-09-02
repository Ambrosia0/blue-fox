package com.ambrosia.profile_service.keycloak.service.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;

import com.ambrosia.profile_service.core.idp.IdpUserService;
import com.ambrosia.profile_service.keycloak.dto.CredentialRepresentation;
import com.ambrosia.profile_service.keycloak.dto.UserRepresentation;
import com.ambrosia.profile_service.keycloak.service.KeycloakAdminClient;
import com.ambrosia.profile_service.user.model.dto.request.FirstLastName;
import com.ambrosia.profile_service.user.model.entity.User;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service used for managing user information through Keycloak Admin REST API {@link KeycloakAdminClient}
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class KeycloakUserServiceImpl implements IdpUserService{
    private final KeycloakAdminClient keycloakService;

    /**
     * Creates user through keycloak admin api.
     * Used for testing purposes
     */
    @Override
    public void registerUser(User user) {
        Assert.notNull(user.getPassword(), "Password must not be null!");
        Assert.notNull(user.getId(), "User id must not be null!");
        keycloakService.register(UserRepresentation.builder()
            .id(user.getId().toString())
            .username(user.getUsername())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .enabled(true)
            .credentials(
                List.of(
                    CredentialRepresentation.builder()
                    .type("password")
                    .value(user.getPassword())
                    .temporary(false)
                    .build()       
                )
            )
            .requiredActions(List.of(
                "VERIFY_EMAIL"
            ))
            .emailVerified(false)
            .build());
    }

    /**
     * To edit username property "editUsernameAllowed" should be setted to 'true'
     * Avoid setting registrationEmailAsUsername OR email as username in GUI to true,
     * keycloak overrides username with email
     */
    @Override
    public void updateUsername(UUID userId, String username) {
        var user = keycloakService.get(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't update username!"));
        user.setUsername(username);
        keycloakService.update(user);
    }

    @Override
    public void updateFirstLastName(UUID userId, FirstLastName firstLastName) {
        var user = keycloakService.get(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't update username!"));
        user.setFirstName(firstLastName.firstName());
        user.setLastName(firstLastName.lastName());
        keycloakService.update(user);
    }

    /**
     * Updates avatarId
     * Requires to add attribute in 'User Profile' settings of the realm
     */
    @Override
    public void updateAvatar(UUID userId, @Nullable String avatarId) {
        var user = keycloakService.get(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't update avatar!"));
        var attributes = user.getAttributes();
        if(attributes != null){
            if(avatarId != null)
                attributes.put("avatarId", List.of(avatarId));
            else
                attributes.remove("avatarId");
        }else if(attributes == null){
            if(avatarId != null)
                user.setAttributes(Map.of(
                    "avatarId", List.of(avatarId)
                ));
            else
                return;
        }
        keycloakService.update(user);
    }
}
