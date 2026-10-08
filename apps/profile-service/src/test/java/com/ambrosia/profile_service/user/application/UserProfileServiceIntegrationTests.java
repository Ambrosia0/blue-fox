package com.ambrosia.profile_service.user.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.awaitility.Awaitility.await;

import java.net.URI;
import java.nio.file.Files;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.web.client.RestClient;

import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;
import com.ambrosia.library_s3.TestS3Configuration;
import com.ambrosia.profile_service.BaseIntegrationTest;
import com.ambrosia.profile_service.UserRegistration;
import com.ambrosia.profile_service.user.api.dto.request.FirstLastName;
import com.ambrosia.profile_service.user.application.query.UserQueryService;
import com.ambrosia.profile_service.user.domain.repository.UsernameHistoryRepository;
import com.ambrosia.profile_service.user.infrastructure.UserProjectionService;
import com.ambrosia.profile_service.user.infrastructure.elastic.ElasticUserRepository;
import com.ambrosia.profile_service.user.infrastructure.keycloak.service.KeycloakAdminClient;
import com.ambrosia.profile_service.user.infrastructure.persistence.JdbcUserRepository;
import com.ambrosia.profile_service.util.FileMetadataFactory;
import com.ambrosia.profile_service.util.UserCreator;

@Import({
    TestS3Configuration.class
})
public class UserProfileServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired JdbcUserRepository userRepository;
    @Autowired UserRegistration userRegistration;
    @Autowired ElasticUserRepository elasticUserRepository;
    @Autowired UserProfileService userProfileService;
    @Autowired UsernameHistoryRepository usernameHistoryRepository;
    @Autowired KeycloakAdminClient keycloakAdminService;
    @Autowired UserQueryService userQueryService;

    @Autowired @Qualifier("testRestClient") RestClient testRestClient;
    @MockitoSpyBean UserProjectionService userProjectionService;

    @Autowired UserCreator userCreator;

    private int AWAIT_DURATION = 10;
    
    @Test
    void shouldSetAboutText(){
        var user = userCreator.create();
        var initialText = user.getAbout();
        assertDoesNotThrow(
            () -> userProfileService.setAboutText(
                Actor.builder()
                    .id(user.getId())
                    .role(Role.USER)
                    .build(), 
                "Test about text"
            )
        );
        assertNotEquals(initialText, userRepository.findById(user.getId()).get().getAbout());
    }

    @Test
    void shouldReturnPublicUserProfile(){
        var user = userCreator.create();
        assertNotNull(
            userQueryService.getPublicProfile(
                user.getUsername(), 
                Actor.builder()
                    .role(Role.ANONYMOUS)
                    .build()
            )
        );
    }

    @Test
    void shouldReturnCurrentUserProfile(){
        var user = userCreator.create();
        assertNotNull(userQueryService.getProfile(
                Actor.builder()
                    .id(user.getId())
                    .role(Role.USER)
                    .build()
            )
        );
    }

    @Test
    void shouldChangeUsername(){
        var user = userCreator.create();
        var username = "TestUnexisting".toLowerCase();
        assertDoesNotThrow(
            () -> userProfileService.updateUsername(
                Actor.builder()
                    .id(user.getId())
                    .role(Role.USER)
                    .build(), 
                username
            )
        );
        await().pollDelay(Duration.ofSeconds(3)).atMost(Duration.ofSeconds(AWAIT_DURATION)).untilAsserted(
            () -> assertEquals(username, userRepository.findById(user.getId()).get().getUsername())
        );
    }

    @Test
    void shouldChangeFirstAndLastName(){
        var user = userCreator.create();
        var firstName = "TestFirst";
        var lastName = "TestLast";
        assertDoesNotThrow(() -> userProfileService.updateFirstLastName(
            Actor.builder()
                .id(user.getId())
                .role(Role.USER)
                .build(),
            new FirstLastName(firstName, lastName)
        ));
        await().pollDelay(Duration.ofSeconds(3)).atMost(Duration.ofSeconds(AWAIT_DURATION))
            .untilAsserted(
                () -> {
                    var dbUser = userRepository.findById(user.getId()).get();
                    assertEquals(firstName, dbUser.getFirstName());
                    assertEquals(lastName, dbUser.getLastName());
                }
            );
    }

    @Test
    void shouldUpdateAvatarThenDeleteAvatar() throws Exception{
        var user = userCreator.create();
        var file = FileMetadataFactory.fileMetadata();
        var resp = userProfileService.updateAvatar(
            Actor.builder()
                .id(user.getId())
                .role(Role.USER)
                .build(), 
            file
        );
        testRestClient.put()
            .uri(URI.create(resp.uploadUrl()))
            .contentType(MediaType.parseMediaType(file.contentType().getMimeType()))
            .contentLength(file.fileSize())
            .header("x-amz-checksum-md5", file.md5())
            .body(Files.readAllBytes(FileMetadataFactory.testImagePath))
            .retrieve()
            .toBodilessEntity();
        assertDoesNotThrow(() -> userProfileService.confirmAvatarUpload(
                Actor.builder()
                    .id(user.getId())
                    .role(Role.USER)
                    .build(), 
                resp.avatarId()
            )
        );
        await().pollInterval(Duration.ofSeconds(3)).atMost(Duration.ofSeconds(AWAIT_DURATION))
            .untilAsserted(() -> assertNotNull(userRepository.findById(user.getId()).get().getAvatarId()));
        assertDoesNotThrow(
            () -> userProfileService.updateAvatar(
                Actor.builder()
                    .id(user.getId())
                    .role(Role.USER)
                    .build(), 
                null
            )
        );
        await().pollInterval(Duration.ofSeconds(3)).atMost(Duration.ofSeconds(AWAIT_DURATION))
            .untilAsserted(() -> assertNull(userRepository.findById(user.getId()).get().getAvatarId()));
    }

    @Test
    void shouldUpdateUsername(){
        var user = userCreator.create();
        var username = "TestUsernameProfile".toLowerCase();
        userProfileService.updateUsername(
            Actor.builder()
                .id(user.getId())
                .role(Role.USER)
                .build(),
            username
        );
        await()
            .pollDelay(Duration.ofSeconds(2))
            .atMost(Duration.ofSeconds(6))
            .untilAsserted(() -> assertEquals(username, userRepository.findById(user.getId()).get().getUsername()));
    }
}
