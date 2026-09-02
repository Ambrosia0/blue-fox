package com.ambrosia.comment_service.grpc.impl;

import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ambrosia.comment_service.grpc.CommunityService;
import com.ambrosia.community_service.grpc.ScopeCheckRequest;
import com.ambrosia.community_service.grpc.CommunityServiceGrpc.CommunityServiceBlockingStub;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommunityServiceImpl implements CommunityService{
    private final CommunityServiceBlockingStub communityServiceBlockingStub;

    @CircuitBreaker(name = "community-service")
    @Cacheable("community-scope")
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
    private boolean fallback(UUID userId, String scope, Exception e){
        return false;
    }
}
