package com.ambrosia.profile_service.keycloak.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.profile_service.core.idp.IdpAdminService;
import com.ambrosia.profile_service.exception.api.NotEnoughPermissionsException;
import com.ambrosia.profile_service.exception.api.user.UserDoesntBannedException;
import com.ambrosia.profile_service.exception.api.user.UserDoesntExistException;
import com.ambrosia.profile_service.exception.api.user.UserIsDisabledException;
import com.ambrosia.profile_service.keycloak.service.KeycloakAdminClient;
import com.ambrosia.profile_service.user.repository.UserRepository;
import com.ambrosia.profile_service.user.utils.Role;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KeycloakAdminService implements IdpAdminService{
    private final KeycloakAdminClient keycloakAdminClient;

    private final UserRepository userRepository;

    // use certain client/realm role in group to spot user
    // private final KeycloakConfiguration keycloakConfiguration;
    
    @Override
    public void banUser(UUID userId) {
        var user = keycloakAdminClient.get(userId)
            .orElseThrow(() -> new UserDoesntExistException());
        var existsInDb = userRepository.existsById(userId);
        var role = user.getRealmRoles();
        if(!user.getEnabled())
            throw new UserIsDisabledException();
        if(!existsInDb || (role != null && role.contains(Role.admin.name())))
            throw new NotEnoughPermissionsException();
        user.setEnabled(false);
        keycloakAdminClient.update(user);
    }

    @Override
    public void unbanUser(UUID userId) {
        var user = keycloakAdminClient.get(userId)
            .orElseThrow(() -> new UserDoesntExistException());
        var existsInDb = userRepository.existsById(userId);
        var role = user.getRealmRoles();
        if(user.getEnabled())
            throw new UserDoesntBannedException();
        if(!existsInDb || (role != null && role.contains(Role.admin.name())))
            throw new NotEnoughPermissionsException();
        user.setEnabled(true);
        keycloakAdminClient.update(user);
    }
}
