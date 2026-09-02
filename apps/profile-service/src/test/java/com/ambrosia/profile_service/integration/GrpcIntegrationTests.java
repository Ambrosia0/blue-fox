package com.ambrosia.profile_service.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import com.ambrosia.profile_service.BaseIntegrationTest;
import com.ambrosia.profile_service.grpc.UserBlacklistRequest;
import com.ambrosia.profile_service.grpc.UserExistenceRequest;
import com.ambrosia.profile_service.grpc.ProfileServiceGrpc.ProfileServiceBlockingStub;
import com.ambrosia.profile_service.util.BlacklistCreator;
import com.ambrosia.profile_service.util.UserCreator;
import com.google.protobuf.ByteString;


@Import({BlacklistCreator.class})
@TestPropertySource(properties = { "spring.grpc.client.default-channel.address=localhost:9090"})
public class GrpcIntegrationTests extends BaseIntegrationTest{
    @Autowired ProfileServiceBlockingStub profileServiceBlockingStub;

    @Autowired UserCreator userCreator;

    @Autowired BlacklistCreator blacklistCreator;

    @Test
    void shouldReturnFalseOnExistanceCheck(){
        var resp = profileServiceBlockingStub.isUserExist(createExistanceRequest());
        assertFalse(resp.getIsExist());
    }

    @Test
    void shouldReturnTrueOnExistanceCheck(){
        var user = userCreator.createFromScratch();
        var resp = profileServiceBlockingStub.isUserExist(createExistanceRequest(user.getId()));
        assertTrue(resp.getIsExist());
    }

    @Test
    void shouldReturnEmptyList(){
        var user = userCreator.createFromScratch();
        var resp = profileServiceBlockingStub.getUserBlacklist(
            createBlacklistRequest(user.getId())
        );
        assertTrue(resp.getUserIdList().isEmpty());
    }

    @Test
    void shouldReturnBlacklistedUsers(){
        var blacklist = blacklistCreator.createFromScratch();
        var resp = profileServiceBlockingStub.getUserBlacklist(
            createBlacklistRequest(blacklist.getId().userId())
        );
        assertFalse(resp.getUserIdList().isEmpty());
        var id = resp.getUserIdList().getFirst().asReadOnlyByteBuffer();
        var blacklistedUserId = new UUID(
            id.getLong(),
            id.getLong()
        );
        assertEquals(
            blacklist.getId().blacklistedUserId(), 
            blacklistedUserId
        );
    }

    @AfterEach
    void cleanUp(){
        blacklistCreator.cleanUp();
        userCreator.cleanUp();
    }

    private UserExistenceRequest createExistanceRequest(UUID userId){
        return UserExistenceRequest.newBuilder()
            .setUserId(userId.toString())
            .build();
    }

    private UserExistenceRequest createExistanceRequest(){
        return UserExistenceRequest.newBuilder()
            .setUserId(UUID.randomUUID().toString())
            .build();
    }

    private UserBlacklistRequest createBlacklistRequest(UUID userId){
        return UserBlacklistRequest.newBuilder()
            .setUserId(ByteString.copyFrom(
                    ByteBuffer.allocate(16)
                    .putLong(userId.getMostSignificantBits())
                    .putLong(userId.getLeastSignificantBits())
                    .array()
                )
            )
            .build();
    }
}
