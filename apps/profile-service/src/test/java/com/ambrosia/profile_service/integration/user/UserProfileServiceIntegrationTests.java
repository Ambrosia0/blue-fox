package com.ambrosia.profile_service.integration.user;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.awaitility.Awaitility.await;

import java.net.URI;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.web.client.RestClient;

import com.ambrosia.library_s3.TestS3Configuration;
import com.ambrosia.profile_service.BaseIntegrationTest;
import com.ambrosia.profile_service.UserRegistration;
import com.ambrosia.profile_service.exception.api.user.UserDoesntExistException;
import com.ambrosia.profile_service.exception.api.user.UsernameAlreadyClaimedException;
import com.ambrosia.profile_service.exception.api.user.UsernameChangeIntervalException;
import com.ambrosia.profile_service.keycloak.service.KeycloakAdminClient;
import com.ambrosia.profile_service.user.model.dto.request.FirstLastName;
import com.ambrosia.profile_service.user.model.entity.User;
import com.ambrosia.profile_service.user.model.entity.UsernameHistory;
import com.ambrosia.profile_service.user.repository.UserRepository;
import com.ambrosia.profile_service.user.repository.UsernameHistoryRepository;
import com.ambrosia.profile_service.user.repository.elastic.ElasticUserRepository;
import com.ambrosia.profile_service.user.service.UserProfileService;
import com.ambrosia.profile_service.user.service.UserProjectionService;
import com.ambrosia.profile_service.user.service.UserQueryService;
import com.ambrosia.profile_service.util.Factory;
import com.ambrosia.profile_service.util.FileMetadataFactory;

@Import({
    BaseIntegrationTest.TestKeycloakConfiguration.class,
    TestS3Configuration.class
})
public class UserProfileServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired UserRepository userRepository;
    @Autowired UserRegistration userRegistration;
    @Autowired ElasticUserRepository elasticUserRepository;
    @Autowired UserProfileService userProfileService;
    @Autowired UsernameHistoryRepository usernameHistoryRepository;
    @Autowired KeycloakAdminClient keycloakAdminService;
    @Autowired UserQueryService userQueryService;

    @Autowired @Qualifier("testRestClient") RestClient testRestClient;
    @MockitoSpyBean UserProjectionService userProjectionService;
    
    @AfterAll
    void cleanUp(){
        usernameHistoryRepository.deleteAll();
        var it = userRepository.findAll().iterator();
        while(it.hasNext()){
            var user = it.next();
            keycloakAdminService.delete(user.getId());
        }
        userRepository.deleteAll();
        elasticUserRepository.deleteAll();
    }

    @Test
    void shouldThrowUserDoesntExistExceptionOnSetTextAbout(){
        assertThrows(
            UserDoesntExistException.class,
            () -> userProfileService.setAboutText(UUID.randomUUID(), "Test")
        );
    }

    @Test
    void shouldSetAboutText(){
        var user = registerUser();
        var initialText = user.getAbout();
        assertDoesNotThrow(() -> userProfileService.setAboutText(user.getId(), "Test about text"));
        assertNotEquals(initialText, userRepository.findById(user.getId()).get().getAbout());
    }

    @Test
    void shouldThrowUserDoesntExistOnGetPublicProfile(){
        assertThrows(
            UserDoesntExistException.class,
            () -> userQueryService.getPublicProfile("TestUsernameProfile"+ThreadLocalRandom.current().nextLong(1L, 999_999L), null)
        );
    }

    @Test
    void shouldReturnPublicUserProfile(){
        var user = registerUser();
        assertNotNull(userQueryService.getPublicProfile(user.getUsername(), null));
    }

    @Test
    void shouldReturnCurrentUserProfile(){
        var user = registerUser();
        assertNotNull(userQueryService.getProfile(user.getId()));
    }

    @Test
    void shouldThrowUsernameAlreadyClaimedException(){
        var user1 = registerUser();
        var user2 = registerUser();
        assertThrows(
            UsernameAlreadyClaimedException.class,
            () -> userProfileService.updateUsername(user1.getId(), user2.getUsername())
        );
    }

    @Test
    void shouldThrowUsernameChangeIntervalException(){
        var user = registerUser();
        usernameHistoryRepository.save(UsernameHistory.from(user.getUsername(), user.getId()));
        assertThrows(
            UsernameChangeIntervalException.class,
            () -> userProfileService.updateUsername(user.getId(), "TestUsernameChangeInterval")
        );
    }

    @Test
    void shouldChangeUsername(){
        var user = registerUser();
        var username = "TestUnexisting".toLowerCase();
        assertDoesNotThrow(() -> userProfileService.updateUsername(user.getId(), username));
        await().pollDelay(Duration.ofSeconds(5)).atMost(Duration.ofSeconds(20)).untilAsserted(
            () -> assertEquals(username, userRepository.findById(user.getId()).get().getUsername())
        );
    }

    @Test
    void shouldChangeFirstAndLastName(){
        var user = registerUser();
        var firstName = "TestFirst";
        var lastName = "TestLast";
        assertDoesNotThrow(() -> userProfileService.updateFirstLastName(
            user.getId(),
            new FirstLastName(firstName, lastName)
        ));
        await().pollDelay(Duration.ofSeconds(5)).atMost(Duration.ofSeconds(20))
            .untilAsserted(
                () -> {
                    var dbUser = userRepository.findById(user.getId()).get();
                    assertEquals(firstName, dbUser.getFirstName());
                    assertEquals(lastName, dbUser.getLastName());
                }
            );
    }

    @Test
    void shouldThrowUserDoesntExistExceptionOnChangeAvatar(){
        assertThrows(
            UserDoesntExistException.class,
            () -> userProfileService.updateAvatar(UUID.randomUUID(), FileMetadataFactory.fileMetadata())
        );
    }

    @Test
    void shouldUpdateAvatarThenDeleteAvatar() throws Exception{
        var user = registerUser();
        var file = FileMetadataFactory.fileMetadata();
        var resp = userProfileService.updateAvatar(user.getId(), file);
        testRestClient.put()
            .uri(URI.create(resp.uploadUrl()))
            .contentType(MediaType.parseMediaType(file.contentType().getMimeType()))
            .contentLength(file.fileSize())
            .header("x-amz-checksum-md5", file.md5())
            .body(Files.readAllBytes(FileMetadataFactory.testImagePath))
            .retrieve()
            .toBodilessEntity();
        assertDoesNotThrow(() -> userProfileService.confirmAvatarUpload(user.getId(), resp.avatarId()));
        await().pollInterval(Duration.ofSeconds(5)).atMost(Duration.ofSeconds(20))
            .untilAsserted(() -> assertNotNull(userRepository.findById(user.getId()).get().getAvatarId()));
        assertDoesNotThrow(() -> userProfileService.updateAvatar(user.getId(), null));
        await().pollInterval(Duration.ofSeconds(5)).atMost(Duration.ofSeconds(20))
            .untilAsserted(() -> assertNull(userRepository.findById(user.getId()).get().getAvatarId()));
    }

    @Test
    void shouldReturnEmptyList(){
        assertTrue(userProfileService.getUserInfo(List.of(UUID.randomUUID())).isEmpty());
    }

    @Test
    void shouldNotReturnEmptyList(){
        var user1 = registerUser();
        var user2 = registerUser();
        assertFalse(userProfileService.getUserInfo(List.of(user1.getId(), user2.getId())).isEmpty());
    }

    @Test
    void shouldUpdateUsername(){
        var user = registerUser();
        var username = "TestUsernameProfile".toLowerCase();
        userProfileService.updateUsername(user.getId(), username);
        await()
            .pollDelay(Duration.ofSeconds(2))
            .atMost(Duration.ofSeconds(6))
            .untilAsserted(() -> assertEquals(username, userRepository.findById(user.getId()).get().getUsername()));
    }

    private User registerUser(){
        try {
            var user = Factory.createUser();
            userRegistration.register(user);
            await()
                .pollDelay(Duration.ofSeconds(2))
                .atMost(Duration.ofSeconds(6))
                .untilAsserted(() -> assertTrue(userRepository.findByUsernameIgnoreCase(user.getUsername()).isPresent()));
            return userRepository.findByUsernameIgnoreCase(user.getUsername()).get();
        } catch (Exception e) {
            throw new RuntimeException("Can't register user!", e);
        }
    }
}
