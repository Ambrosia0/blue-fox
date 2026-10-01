package com.ambrosia.community_service.integration.community;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.client.RestClient;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.community.model.dto.request.CommunityEdit;
import com.ambrosia.community_service.community.repository.CommunityRepository;
import com.ambrosia.community_service.community.service.CommunityManageService;
import com.ambrosia.community_service.community.utils.ScopeEnum;
import com.ambrosia.community_service.community.utils.policy.UserActor;
import com.ambrosia.community_service.exception.community.CommunityDoesntExistException;
import com.ambrosia.community_service.exception.community.ExceededOwnedCommunityLimitException;
import com.ambrosia.community_service.exception.community.NotEnoughPermissionsException;
import com.ambrosia.community_service.exception.community.UserDoesntExistException;
import com.ambrosia.community_service.exception.community.UserIsBannedException;
import com.ambrosia.community_service.exception.community.UserIsOwnerException;
import com.ambrosia.community_service.utils.CommunityCreator;
import com.ambrosia.community_service.utils.Factory;
import com.ambrosia.community_service.utils.FileMetadataFactory;
import com.ambrosia.community_service.utils.UserBanCreator;
import com.ambrosia.community_service.utils.UserCreator;
import com.ambrosia.library_s3.TestS3Configuration;

@Import({TestS3Configuration.class})
@Transactional
public class CommunityManageServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired CommunityRepository communityRepository;
    @Autowired CommunityManageService communityManageService;
    @Autowired UserBanCreator userBanCreator;
    @Autowired RestClient testRestClient;

    @Autowired UserCreator userCreator;
    @Autowired CommunityCreator communityCreator;

    @Test
    void shouldCreateCommunity(){
        var user = userCreator.create();
        var community = Factory.createRequest("TestCommunity", "TestCommunity", false);
        assertDoesNotThrow(
            () -> communityManageService.createCommunity(community, user.getId())
        );
    }

    @Test
    void shouldThrowExceededOwnedCommunityLimitException(){
        var user = userCreator.create();
        communityCreator.createCommunity(user.getId(), false);
        communityCreator.createCommunity(user.getId(), false);
        communityCreator.createCommunity(user.getId(), false);
        
        var num = ThreadLocalRandom.current().nextLong(1L, 999_999L);
        assertThrows(
            ExceededOwnedCommunityLimitException.class,
            () -> communityManageService.createCommunity(
                Factory.createRequest("TestCommunity"+num, "TestCommunity"+num, false), 
                user.getId()
            )
        );
    }

    @Test
    void shouldThrowCommunityDoesntExistsExceptionOnEditInfo(){
        assertThrows(
            CommunityDoesntExistException.class,
            () -> communityManageService.editCommunityInfo(
                ThreadLocalRandom.current().nextLong(),
                CommunityEdit.builder().displayedName("Test").build(),
                new UserActor(UUID.randomUUID())
            )
        );
    }

    @Test
    void shouldThrowNotEnoughPermissionsExceptionOnEditInfo(){
        var community = communityCreator.createCommunity(false);
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> communityManageService.editCommunityInfo(
                community.getId(), 
                CommunityEdit.builder().displayedName("TestTest").build(), 
                new UserActor(UUID.randomUUID())
            )
        );
    }

    @Test
    void shouldEditCommunityInfo(){
        var community = communityCreator.createCommunity(false);
        var name = "Test name";
        assertDoesNotThrow(
            () -> communityManageService.editCommunityInfo(
                community.getId(), 
                CommunityEdit.builder().displayedName(name).build(),
                new UserActor(community.getOwnerId())
            )
        );
        assertEquals(name, communityRepository.findById(community.getId()).get().getDisplayedName());
    }

    @Test
    void shouldThrowCommunityDoesntExistExceptionOnScopeEdit(){
        var user = userCreator.create();
        assertThrows(
            CommunityDoesntExistException.class,
            () -> communityManageService.editCommunityInfo(
                ThreadLocalRandom.current().nextLong(), 
                CommunityEdit.builder()
                    .scopes(Map.of(
                        user.getId(), Set.of(ScopeEnum.USER_BAN)
                    ))
                    .build(), 
                new UserActor(UUID.randomUUID())
            )
        );
    }

    @Test
    void shouldThrowNotEnoughPermissionsExceptionOnScopeEdit(){
        var community = communityCreator.createCommunity(false);
        var user = userCreator.create();
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> communityManageService.editCommunityInfo(
                community.getId(), 
                CommunityEdit.builder()
                    .scopes(Map.of(
                        user.getId(), Set.of(ScopeEnum.USER_BAN)
                    ))
                    .build(),
                new UserActor(user.getId())
            )
        );
    }

    @Test
    void shouldThrowUserIsOwnerExceptionOnScopeEdit(){
        var community = communityCreator.createCommunity(false);
        assertThrows(
            UserIsOwnerException.class,
            () -> communityManageService.editCommunityInfo(
                community.getId(), 
                CommunityEdit.builder()
                    .scopes(Map.of(
                        community.getOwnerId(), Set.of(ScopeEnum.USER_BAN)
                    ))
                    .build(), 
                new UserActor(community.getOwnerId())
            )
        );
    }

    @Test
    void shouldThrowUserDoesntExistException(){
        var community = communityCreator.createCommunity(false);
        assertThrows(
            UserDoesntExistException.class,
            () -> communityManageService.editCommunityInfo(
                community.getId(), 
                CommunityEdit.builder()
                    .scopes(Map.of(
                        UUID.randomUUID(), Set.of(ScopeEnum.USER_BAN)
                    ))
                    .build(), 
                new UserActor(community.getOwnerId())
            )
        );
    }

    @Test
    void shouldThrowUserIsBannedException(){
        var community = communityCreator.createCommunity(false);
        var user = userCreator.create();
        communityCreator.createBan(community.getId(), user.getId());
        assertThrows(
            UserIsBannedException.class,
            () -> communityManageService.editCommunityInfo(
                community.getId(),
                CommunityEdit.builder()
                    .scopes(Map.of(
                        user.getId(), Set.of(ScopeEnum.USER_BAN)
                    ))
                    .build(),
                new UserActor(community.getOwnerId())
            )
        );
    }

    @Test
    void shouldEditCommunityScope(){
        var community = communityCreator.createCommunity(false);
        var user = userCreator.create();
        assertDoesNotThrow(
            () -> communityManageService.editCommunityInfo(
                community.getId(), 
                CommunityEdit.builder()
                    .scopes(Map.of(
                        user.getId(), Set.of(ScopeEnum.USER_BAN)
                    ))
                    .build(), 
                new UserActor(community.getOwnerId())
            )
        );
        assertTrue(
            communityRepository.scopeExists(
                    community.getId(), 
                    ScopeEnum.USER_BAN.getId(),
                    user.getId()
            )
        );
        assertDoesNotThrow(
            () -> communityManageService.editCommunityInfo(
                community.getId(),
                CommunityEdit.builder()
                    .scopes(Map.of())
                    .build(),
                new UserActor(community.getOwnerId())
            )
        );
        assertFalse(
            communityRepository.scopeExists(
                    community.getId(), 
                    ScopeEnum.USER_BAN.getId(),
                    user.getId()
            )
        );
    }

    @Test 
    void shouldThrowCommunityDoesntExistOnUploadAvatar(){
        assertThrows(
            CommunityDoesntExistException.class,
            () -> communityManageService.uploadAvatar(
                ThreadLocalRandom.current().nextLong(),
                FileMetadataFactory.fileMetadata(), 
                new UserActor(UUID.randomUUID())
            )
        );
    }

    @Test 
    void shouldThrowNotEnoughPermissionsOnUploadAvatar(){
        var community = communityCreator.createCommunity(false);
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> communityManageService.uploadAvatar(
                community.getId(),
                FileMetadataFactory.fileMetadata(), 
                new UserActor(UUID.randomUUID())
            )
        );
    }

    @Test 
    void shouldUploadAvatarThenDeleteAvatar() throws IOException{
        var community = communityCreator.createCommunity(false);
        var file = FileMetadataFactory.fileMetadata();
        var resp = communityManageService.uploadAvatar(
            community.getId(),
            file,
            new UserActor(community.getOwnerId())
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
                resp.avatarId(),
                new UserActor(community.getOwnerId())
            )
        );
        assertNotNull(communityRepository.findById(community.getId()).get().getAvatarId());
        assertDoesNotThrow(() -> communityManageService.uploadAvatar(
            community.getId(), 
            null, 
            new UserActor(community.getOwnerId()))
        );
        assertNull(communityRepository.findById(community.getId()).get().getAvatarId());
    }

    @Test
    void shouldDeleteCommunity(){
        var community = communityCreator.createCommunity(false);
        assertDoesNotThrow(
            () -> communityManageService.deleteCommunity(
                community.getId(),
                new UserActor(community.getOwnerId())
            )
        );
        assertFalse(communityRepository.findById(community.getId()).isPresent());
    }
}
