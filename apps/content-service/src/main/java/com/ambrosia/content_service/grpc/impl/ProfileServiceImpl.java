package com.ambrosia.content_service.grpc.impl;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ambrosia.content_service.grpc.ProfileService;
import com.ambrosia.profile_service.grpc.UserBlacklistRequest;
import com.ambrosia.profile_service.grpc.UserExistenceRequest;
import com.ambrosia.profile_service.grpc.ProfileServiceGrpc.ProfileServiceBlockingStub;
import com.google.protobuf.ByteString;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileServiceImpl implements ProfileService{
    private final ProfileServiceBlockingStub profileServiceBlockingStub;

    @Override
    public boolean isUserExist(UUID userId) {
        return profileServiceBlockingStub.isUserExist(UserExistenceRequest.newBuilder()
            .setUserId(userId.toString())
            .build()
        ).getIsExist();
    }

    @CircuitBreaker(name = "profile-service", fallbackMethod = "fallback")
    @Cacheable(cacheNames = "blacklist", key = "#userId", unless = "#result == null")
    @Override
    public List<UUID> getBlacklist(UUID userId) {
        try {
            return profileServiceBlockingStub.getUserBlacklist(UserBlacklistRequest.newBuilder()
                .setUserId(ByteString.copyFrom(
                    ByteBuffer.allocate(16)
                    .putLong(userId.getMostSignificantBits())
                    .putLong(userId.getLeastSignificantBits())
                    .array())
                )
                .build()
            )
            .getUserIdList()
            .stream()
            .map(byteString -> {
                var buf = ByteBuffer.wrap(byteString.toByteArray());
                return new UUID(
                    buf.getLong(),
                    buf.getLong()
                );
            })
            .toList();
        } catch (Exception e) {
            log.error("Profile service unavailable!");
            return null;
        }
    }

    @SuppressWarnings("unused")
    private List<UUID> fallback(UUID userId, Exception e){
        throw new RuntimeException(e);
    }
}
