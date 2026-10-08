package com.ambrosia.community_service.community.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.client.RestClient;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.community.api.dto.request.CommunityEdit;
import com.ambrosia.community_service.community.domain.repository.CommunityRepository;
import com.ambrosia.community_service.community.utils.ScopeEnum;
import com.ambrosia.community_service.utils.CommunityCreator;
import com.ambrosia.community_service.utils.Factory;
import com.ambrosia.community_service.utils.FileMetadataFactory;
import com.ambrosia.community_service.utils.UserCreator;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;
import com.ambrosia.library_s3.TestS3Configuration;

@Import({TestS3Configuration.class})
@Transactional
public class CommunityManageServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired CommunityRepository communityRepository;
    @Autowired CommunityManageService communityManageService;
    @Autowired RestClient testRestClient;

    @Autowired UserCreator userCreator;
    @Autowired CommunityCreator communityCreator;

    @Test
    void shouldCreateCommunityThenDeleteCommunity(){
        var user = userCreator.create();
        var community = Factory.createRequest("TestCommunity", "TestCommunity", false);

        var res = communityManageService.createCommunity(community, new Actor(user.getId(), Role.USER));
        assertDoesNotThrow(
            () -> communityRepository.findById(res.id()).get()
        );

        assertDoesNotThrow(
            () -> communityManageService.deleteCommunity(res.id(), new Actor(user.getId(), Role.USER))
        );

        assertTrue(communityRepository.findById(res.id()).isEmpty());
    }

    @Test
    void shouldEditCommunityInfo(){
        var community = communityCreator.createCommunity(false);
        var name = "Test name";

        var user1 = userCreator.create();
        var user2 = userCreator.create();
        assertDoesNotThrow(
            () -> communityManageService.editCommunityInfo(
                community.getId(), 
                CommunityEdit.builder()
                    .displayedName(name)
                    .scopes(Map.of(
                        user1.getId(), Set.of(ScopeEnum.USER_BAN, ScopeEnum.COMMENT_DELETE),
                        user2.getId(), Set.of(ScopeEnum.POST_DELETE, ScopeEnum.FOLLOW_MANAGE)
                    ))
                    .build(),
                new Actor(community.getOwnerId(), Role.USER)
            )
        );
        assertEquals(name, communityRepository.findById(community.getId()).get().getDisplayedName());
    }

    @Test 
    void shouldUploadAvatarThenDeleteAvatar() throws IOException{
        var community = communityCreator.createCommunity(false);
        var file = FileMetadataFactory.fileMetadata();
        var resp = communityManageService.uploadAvatar(
            community.getId(),
            file,
            new Actor(community.getOwnerId(), Role.USER)
        );
        testRestClient.put()
            .uri(URI.create(resp.uploadUrl()))
            .contentType(MediaType.parseMediaType(file.contentType().getMimeType()))
            .contentLength(file.fileSize())
            .header("x-amz-checksum-md5", file.md5())
            .body(Files.readAllBytes(FileMetadataFactory.testImagePath))
            .retrieve()
            .toBodilessEntity();

        assertDoesNotThrow(
            () -> communityManageService.validateAvatarUpload(
                community.getId(),
                resp.avatarId()
            )
        );
        assertNotNull(communityRepository.findById(community.getId()).get().getAvatarId());
        assertDoesNotThrow(() -> communityManageService.uploadAvatar(
                community.getId(), 
                null, 
                new Actor(community.getOwnerId(), Role.USER)
            )
        );
        assertNull(communityRepository.findById(community.getId()).get().getAvatarId());
    }
}
