package com.ambrosia.profile_service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import com.ambrosia.profile_service.core.idp.IdpUserService;
import com.ambrosia.profile_service.exception.api.user.UsernameAlreadyClaimedException;
import com.ambrosia.profile_service.user.model.entity.User;
import com.ambrosia.profile_service.user.repository.UserRepository;


@TestComponent
public class UserRegistration {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IdpUserService keycloakUserService;

    public void register(User user) {
        var exists = userRepository.existsByUsernameOrEmail(user.getUsername(), user.getEmail());
        if(exists)
            throw new UsernameAlreadyClaimedException("Username/email already used!");
        keycloakUserService.registerUser(user);
    }
}
