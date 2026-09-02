package com.ambrosia.community_service.grpc;

import java.util.List;
import java.util.UUID;

import org.springframework.grpc.server.service.GrpcService;

import com.ambrosia.profile_service.grpc.UserExistenceRequest;
import com.ambrosia.profile_service.grpc.UsersExistenceRequest;
import com.ambrosia.profile_service.grpc.ProfileServiceGrpc.ProfileServiceBlockingStub;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@GrpcService
public class ProfileService {
    private final ProfileServiceBlockingStub profileServiceBlockingStub;

    public boolean isUserExists(UUID userId){
        return profileServiceBlockingStub.isUserExist(UserExistenceRequest.newBuilder()
            .setUserId(userId.toString())
            .build()
        )
        .getIsExist();
    }

    public boolean isUsersExists(List<UUID> userIds){
        return profileServiceBlockingStub.isUsersExist(UsersExistenceRequest.newBuilder()
            .addAllUserId(userIds.stream().map(t -> t.toString()).toList())
            .build()
        )
        .getIsExist();
    }
}
