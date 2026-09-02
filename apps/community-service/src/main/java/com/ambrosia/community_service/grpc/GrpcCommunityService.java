package com.ambrosia.community_service.grpc;

import java.util.UUID;

import org.springframework.grpc.server.service.GrpcService;

import com.ambrosia.community_service.community.model.entity.keys.ScopeLinkKey;
import com.ambrosia.community_service.community.repository.ScopeLinkRepository;
import com.ambrosia.community_service.community.utils.ScopeEnum;
import com.ambrosia.community_service.grpc.CommunityServiceGrpc.CommunityServiceImplBase;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@GrpcService
public class GrpcCommunityService extends CommunityServiceImplBase{
    private final ScopeLinkRepository scopeLinkRepository;

    @Override
    public void isUserAllowed(ScopeCheckRequest request, StreamObserver<ScopeCheckResponse> responseObserver) {
        var exist = scopeLinkRepository.existsById(
            ScopeLinkKey.create(
                UUID.fromString(request.getUserId()),
                ScopeEnum.valueOf(request.getScope()).getId(),
                request.getCommunityId()
            ));
        var resp = ScopeCheckResponse.newBuilder().setIsAllowed(exist).build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }
}
