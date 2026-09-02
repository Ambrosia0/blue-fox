package com.ambrosia.community_service.integration.community;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.client.RestClient;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.community.model.dto.request.ScopePair;
import com.ambrosia.community_service.community.model.entity.Community;
import com.ambrosia.community_service.community.model.entity.ScopeLink;
import com.ambrosia.community_service.community.repository.CommunityRepository;
import com.ambrosia.community_service.community.repository.ScopeLinkRepository;
import com.ambrosia.community_service.community.service.CommunityManageService;
import com.ambrosia.community_service.community.utils.ScopeEnum;
import com.ambrosia.community_service.community.utils.policy.UserActor;
import com.ambrosia.community_service.exception.community.CommunityDoesntExistException;
import com.ambrosia.community_service.exception.community.ExceededOwnedCommunityLimitException;
import com.ambrosia.community_service.exception.community.NotEnoughPermissionsException;
import com.ambrosia.community_service.exception.community.UserDoesntExistException;
import com.ambrosia.community_service.exception.community.UserIsBannedException;
import com.ambrosia.community_service.exception.community.UserIsOwnerException;
import com.ambrosia.community_service.grpc.ProfileService;
import com.ambrosia.community_service.utils.CommunityEditBuilder;
import com.ambrosia.community_service.utils.Factory;
import com.ambrosia.community_service.utils.FileMetadataFactory;
import com.ambrosia.community_service.utils.UserBanCreator;
import com.ambrosia.library_s3.TestS3Configuration;

@Import(TestS3Configuration.class)
@Transactional
public class CommunityManageServiceIntegrationTests extends BaseIntegrationTest{
    @MockitoBean ProfileService profileService;
    @Autowired CommunityRepository communityRepository;
    @Autowired CommunityManageService communityManageService;
    @Autowired ScopeLinkRepository scopeLinkRepository;
    @Autowired UserBanCreator userBanCreator;
    @Autowired RestClient testRestClient;
    

    @BeforeEach
    void init(){
        when(profileService.isUserExists(any(UUID.class))).thenReturn(false);
    }

    @Test
    void shouldCreateCommunity(){
        var userId = UUID.randomUUID();
        var community = Factory.createRequest("TestCommunity", "TestCommunity", false);
        assertDoesNotThrow(
            () -> communityManageService.createCommunity(community, userId)
        );
    }

    @Test
    void shouldThrowExceededOwnedCommunityLimitException(){
        var userId = UUID.randomUUID();
        createCommunity(userId);
        createCommunity(userId);
        createCommunity(userId);
        
        var num = ThreadLocalRandom.current().nextLong(1L, 999_999L);
        assertThrows(
            ExceededOwnedCommunityLimitException.class,
            () -> communityManageService.createCommunity(
                Factory.createRequest("TestCommunity"+num, "TestCommunity"+num, false), userId)
        );
    }

    @Test
    void shouldThrowCommunityDoesntExistsExceptionOnEditInfo(){
        assertThrows(
            CommunityDoesntExistException.class,
            () -> communityManageService.editCommunityInfo(
                ThreadLocalRandom.current().nextLong(),
                CommunityEditBuilder.builder().setDisplayedName("Test").build(),
                new UserActor(UUID.randomUUID())
            )
        );
    }

    @Test
    void shouldThrowNotEnoughPermissionsExceptionOnEditInfo(){
        var community = createCommunity();
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> communityManageService.editCommunityInfo(
                community.getId(), 
                CommunityEditBuilder.builder().setDisplayedName("TestTest").build(), 
                new UserActor(UUID.randomUUID())
            )
        );
    }

    @Test
    void shouldEditCommunityInfo(){
        var community = createCommunity();
        var name = "Test name";
        assertDoesNotThrow(
            () -> communityManageService.editCommunityInfo(
                community.getId(), 
                CommunityEditBuilder.builder().setDisplayedName(name).build(),
                new UserActor(community.getOwnerId())
            )
        );
        assertEquals(name, communityRepository.findById(community.getId()).get().getDisplayedName());
    }

    @Test
    void shouldThrowCommunityDoesntExistExceptionOnScopeEdit(){
        assertThrows(
            CommunityDoesntExistException.class,
            () -> communityManageService.editCommunityScopes(
                ThreadLocalRandom.current().nextLong(), 
                null, 
                new UserActor(UUID.randomUUID())
            )
        );
    }

    @Test
    void shouldThrowNotEnoughPermissionsExceptionOnScopeEdit(){
        var community = createCommunity();
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> communityManageService.editCommunityScopes(
                community.getId(), 
                null,
                new UserActor(UUID.randomUUID())
            )
        );
    }

    @Test
    void shouldThrowUserIsOwnerExceptionOnScopeEdit(){
        var community = createCommunity();
        assertThrows(
            UserIsOwnerException.class,
            () -> communityManageService.editCommunityScopes(
                community.getId(), 
                List.of(new ScopePair(community.getOwnerId(), List.of(ScopeEnum.POST_DELETE))), 
                new UserActor(community.getOwnerId())
            )
        );
    }

    @Test
    void shouldThrowUserDoesntExistException(){
        var community = createCommunity();
        assertThrows(
            UserDoesntExistException.class,
            () -> communityManageService.editCommunityScopes(
                community.getId(), 
                List.of(new ScopePair(UUID.randomUUID(), List.of(ScopeEnum.POST_DELETE))), 
                new UserActor(community.getOwnerId())
            )
        );
    }

    @Test
    void shouldThrowUserIsBannedException(){
        var community = createCommunity();
        var userId = UUID.randomUUID();
        userBanCreator.create(community.getId(), userId);
        assertThrows(
            UserIsBannedException.class,
            () -> communityManageService.editCommunityScopes(
                community.getId(),
                List.of(new ScopePair(userId, List.of(ScopeEnum.USER_BAN))),
                new UserActor(community.getOwnerId())
            )
        );
    }

    @Test
    void shouldEditCommunityScope(){
        when(profileService.isUsersExists(anyList())).thenReturn(true);
        var community = createCommunity();
        var userId = UUID.randomUUID();
        assertDoesNotThrow(
            () -> communityManageService.editCommunityScopes(
                community.getId(), 
                List.of(new ScopePair(userId, List.of(ScopeEnum.POST_DELETE))), 
                new UserActor(community.getOwnerId())
            )
        );
        assertEquals(
            ScopeEnum.POST_DELETE,
            scopeLinkRepository.findByUserIdAndCommunityId(userId, community.getId())
                .get()
                .scopes()[0]
        );
        assertDoesNotThrow(
            () -> communityManageService.editCommunityScopes(
                community.getId(),
                List.<ScopePair>of(),
                new UserActor(community.getOwnerId())
            )
        );
        assertTrue(scopeLinkRepository.findByUserIdAndCommunityId(userId, community.getId()).isEmpty());
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
        var community = createCommunity();
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
        var community = createCommunity();
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
        var community = createCommunity();
        assertDoesNotThrow(
            () -> communityManageService.deleteCommunity(
                community.getId(),
                new UserActor(community.getOwnerId())
            )
        );
        assertFalse(communityRepository.findById(community.getId()).isPresent());
    }

    private Community createCommunity(UUID ownerId){
        var name = "TestCommunity"+ThreadLocalRandom.current().nextLong(1L, 999_999L);
        var community = communityRepository.save(Community.builder()
            .displayedName(name)
            .slug(name)
            .ownerId(ownerId)
            .build()
        );
        scopeLinkRepository.saveAll(
            Arrays.asList(ScopeEnum.values())
                .stream()
                .map(scope -> ScopeLink.create(ownerId, scope.getId(), community.getId()))
                .toList()
        );
        return community;
    }

    private Community createCommunity(){
        var userId = UUID.randomUUID();
        var name = "TestCommunity"+ThreadLocalRandom.current().nextLong(1L, 999_999L);
        var community = communityRepository.save(Community.builder()
            .displayedName(name)
            .slug(name)
            .ownerId(userId)
            .build()
        ); 
        scopeLinkRepository.saveAll(
            Arrays.asList(ScopeEnum.values())
                .stream()
                .map(scope -> ScopeLink.create(userId, scope.getId(), community.getId()))
                .toList()
        );
        return community;
    }
}
