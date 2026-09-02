package com.ambrosia.comment_service.grpc.impl;

import java.util.Collection;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ambrosia.comment_service.grpc.ProfileService;
import com.ambrosia.profile_service.grpc.UserExistenceRequest;
import com.ambrosia.profile_service.grpc.UsersExistenceRequest;
import com.ambrosia.profile_service.grpc.ProfileServiceGrpc.ProfileServiceBlockingStub;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProfileServiceImpl implements ProfileService{
    private final ProfileServiceBlockingStub profileServiceBlockingStub;

    @Cacheable(cacheNames = "user-existance", key = "#userId")
    @Override
    public boolean isUserExist(UUID userId) {
        return profileServiceBlockingStub.isUserExist(UserExistenceRequest.newBuilder()
            .setUserId(userId.toString())
            .build()
        ).getIsExist();
    }

    @Override
    public boolean isUsersExist(Collection<UUID> userIds) {
        return profileServiceBlockingStub.isUsersExist(UsersExistenceRequest.newBuilder()
            .addAllUserId(userIds.stream().map(id -> id.toString()).toList())
            .build()
        ).getIsExist();
    }
}
