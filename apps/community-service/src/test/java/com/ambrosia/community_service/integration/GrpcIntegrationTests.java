package com.ambrosia.community_service.integration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.community.repository.CommunityBanRepository;
import com.ambrosia.community_service.community.repository.ScopeLinkRepository;
import com.ambrosia.community_service.community.utils.ScopeEnum;
import com.ambrosia.community_service.grpc.ScopeCheckRequest;
import com.ambrosia.community_service.grpc.CommunityServiceGrpc.CommunityServiceBlockingStub;
import com.ambrosia.community_service.utils.CommunityCreator;
import com.ambrosia.community_service.utils.FollowCreator;
import com.ambrosia.community_service.utils.ScopeLinkCreator;
import com.ambrosia.community_service.utils.UserBanCreator;

@TestPropertySource(properties = { "spring.grpc.client.default-channel.address=localhost:9090"})
public class GrpcIntegrationTests extends BaseIntegrationTest{
    @Autowired CommunityServiceBlockingStub communityServiceBlockingStub;

    @Autowired ScopeLinkCreator scopeLinkCreator;
    @Autowired CommunityCreator communityCreator;
    @Autowired FollowCreator followCreator;
    @Autowired UserBanCreator userBanCreator;

    @Autowired ScopeLinkRepository scopeLinkRepository;
    @Autowired CommunityBanRepository communityBanRepository;

    @Test
    void shouldReturnFalseOnIsUserAllowed(){
        var community = communityCreator.createCommunity();
        var resp = communityServiceBlockingStub.isUserAllowed(
            createScopeCheckRequest(
                UUID.randomUUID(),
                community.getId(),
                ScopeEnum.USER_BAN
            )
        );
        assertFalse(resp.getIsAllowed());
    }

    @Test
    void shouldReturnTrueOnIsUserAllowed(){
        var scope = scopeLinkCreator.createFromScratch(ScopeEnum.USER_BAN);
        var resp = communityServiceBlockingStub.isUserAllowed(
            createScopeCheckRequest(
                scope.getId().userId(),
                scope.getId().communityId(),
                ScopeEnum.fromId(scope.getId().scopeId())
            )
        );
        assertTrue(resp.getIsAllowed());
    }

    @AfterEach
    void cleanUp(){
        followCreator.cleanUp();
        scopeLinkCreator.cleanUp();
        userBanCreator.cleanUp();
        communityCreator.cleanUp();
    }

    private ScopeCheckRequest createScopeCheckRequest(UUID userId, Long communityId, ScopeEnum scope){
        return ScopeCheckRequest.newBuilder()
            .setUserId(userId.toString())
            .setScope(scope.name())
            .setCommunityId(communityId)
            .build();
    }

}
