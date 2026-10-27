package com.ambrosia.content_service.user.service.impl;

import com.ambrosia.content_service.user.infrastructure.persistence.UserRepository;
import com.ambrosia.content_service.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    
    @Override
    public boolean isUserExist(UUID userId) {
        return userRepository.existsById(userId);
    }
    
    @Override
    public boolean isUsersExist(Set<UUID> userIds) {
        return userRepository.count(userIds) == userIds.size();
    }
}
