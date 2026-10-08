package com.ambrosia.profile_service.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import com.ambrosia.profile_service.BaseIntegrationTest;
import com.ambrosia.profile_service.blacklist.domain.repository.BlacklistRepository;
import com.ambrosia.profile_service.grpc.UserBlacklistRequest;
import com.ambrosia.profile_service.grpc.ProfileServiceGrpc.ProfileServiceBlockingStub;
import com.ambrosia.profile_service.util.UserCreator;
import com.google.protobuf.ByteString;


@TestPropertySource(properties = { "spring.grpc.client.default-channel.address=localhost:9090"})
public class GrpcIntegrationTests extends BaseIntegrationTest{
    @Autowired ProfileServiceBlockingStub profileServiceBlockingStub;

    @Autowired UserCreator userCreator;

    @Autowired BlacklistRepository blacklistRepository;

    @Test
    void shouldReturnBlacklistedUsers(){
        var user1 = userCreator.create();
        var user2 = userCreator.create();
        blacklistRepository.add(
            user1.getId(),
            user2.getId(),
        ""
        );
        var resp = profileServiceBlockingStub.getUserBlacklist(
            createBlacklistRequest(user1.getId())
        );
        assertFalse(resp.getUserIdList().isEmpty());
        var id = resp.getUserIdList().getFirst().asReadOnlyByteBuffer();
        var blacklistedUserId = new UUID(
            id.getLong(),
            id.getLong()
        );
        assertEquals(
            user2.getId(), 
            blacklistedUserId
        );
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
