package com.ambrosia.content_service.grpc.impl;

import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ambrosia.community_service.grpc.ScopeCheckRequest;
import com.ambrosia.community_service.grpc.CommunityServiceGrpc.CommunityServiceBlockingStub;
import com.ambrosia.content_service.grpc.CommunityService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommunityServiceImpl implements CommunityService{
    private final CommunityServiceBlockingStub communityServiceBlockingStub;

    @CircuitBreaker(name = "content-service", fallbackMethod = "fallback")
    @Cacheable(cacheNames = "content-service-user-scopes")
    @Override
    public boolean isUserAllowed(UUID userId, String scope, long communityId) {
        return communityServiceBlockingStub.isUserAllowed(ScopeCheckRequest.newBuilder()
            .setUserId(userId.toString())
            .setCommunityId(communityId)
            .setScope(scope)
            .build()
        ).getIsAllowed();
    }

    @SuppressWarnings("unused")
    private boolean fallback(UUID userId, String scope, long communityId, Exception exception){
        return false;
    }
}
