package com.ambrosia.profile_service.grpc;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.springframework.grpc.server.service.GrpcService;

import com.ambrosia.profile_service.blacklist.repository.BlacklistRepository;
import com.ambrosia.profile_service.grpc.ProfileServiceGrpc.ProfileServiceImplBase;
import com.ambrosia.profile_service.user.repository.UserRepository;
import com.google.protobuf.ByteString;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@GrpcService
public class GrpcProfileService extends ProfileServiceImplBase{
    private final UserRepository userRepository;
    private final BlacklistRepository blacklistRepository;

    @Override
    public void isUserExist(UserExistenceRequest request, StreamObserver<UserExistenceResponse> responseObserver) {
        var exist = userRepository.existsByIdAndIsEnabledIsTrue(UUID.fromString(request.getUserId()));
        var resp = UserExistenceResponse.newBuilder().setIsExist(exist).build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }

    @Override
    public void isUsersExist(UsersExistenceRequest request, StreamObserver<UserExistenceResponse> responseObserver) {
        var exist = userRepository.existsByIds(request.getUserIdList().stream().map(t -> UUID.fromString(t)).toList());
        var resp = UserExistenceResponse.newBuilder().setIsExist(exist).build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserBlacklist(UserBlacklistRequest request, StreamObserver<UserBlacklistResponse> responseObserver) {
        var resp = UserBlacklistResponse.newBuilder();
        var userIdBuf = request.getUserId().asReadOnlyByteBuffer();
        var userId = new UUID(
            userIdBuf.getLong(),
            userIdBuf.getLong()
        );

        var readBuf = ByteBuffer.allocate(16);
        for(UUID blacklistedUserId: blacklistRepository.findByUserId(userId)){
            resp.addUserId(ByteString.copyFrom(readBuf
                .putLong(blacklistedUserId.getMostSignificantBits())
                .putLong(blacklistedUserId.getLeastSignificantBits())
                .array())
            );
            readBuf.clear();
        }
        responseObserver.onNext(resp.build());
        responseObserver.onCompleted();
    }
}
