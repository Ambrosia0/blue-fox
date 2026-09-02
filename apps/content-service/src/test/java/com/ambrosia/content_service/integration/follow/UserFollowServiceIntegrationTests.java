package com.ambrosia.content_service.integration.follow;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.content_service.BaseIntegrationTest;
import com.ambrosia.content_service.exception.api.AlreadyFollowedException;
import com.ambrosia.content_service.exception.api.DoesntFollowedException;
import com.ambrosia.content_service.exception.api.UserDoesntExistException;
import com.ambrosia.content_service.follow.model.entity.UserFollow;
import com.ambrosia.content_service.follow.model.entity.keys.UserFollowKey;
import com.ambrosia.content_service.follow.repository.UserFollowRepository;
import com.ambrosia.content_service.follow.service.UserFollowService;
import com.ambrosia.content_service.grpc.ProfileService;
import com.ambrosia.content_service.kafka.producer.UserFollowEventProducer;

@Transactional
public class UserFollowServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired UserFollowService userFollowService;
    @MockitoBean ProfileService profileService;
    @MockitoSpyBean UserFollowEventProducer userFollowEventProducer;
    @Autowired UserFollowRepository userFollowRepository;

    @Test
    void shouldThrowUserDoesntExistException(){
        when(profileService.isUserExist(any(UUID.class))).thenReturn(false);
        assertThrows(
            UserDoesntExistException.class, 
            () -> userFollowService.followUser(UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    void shouldThrowAlreadyFollowedException(){
        when(profileService.isUserExist(any(UUID.class))).thenReturn(true);
        var follow = createFollow();
        assertThrows(
            AlreadyFollowedException.class, 
            () -> userFollowService.followUser(follow.getId().userId(), follow.getId().followedUserId()));
    }

    @Test
    void shouldCreateUserFollowAndPublishEvent(){
        when(profileService.isUserExist(any(UUID.class))).thenReturn(true);
        var userId = UUID.randomUUID();
        var followed = UUID.randomUUID();
        assertDoesNotThrow(() -> userFollowService.followUser(userId, followed));
        verify(
            userFollowEventProducer,
            times(1)
        ).on(any());
        assertTrue(userFollowRepository.findById(UserFollowKey.create(userId, followed)).isPresent());
    }

    @Test
    void shouldThrowDoesntFollowedException(){
        assertThrows(
            DoesntFollowedException.class,
            () -> userFollowService.removeFollow(UUID.randomUUID(), UUID.randomUUID())
        );
    }

    @Test
    void shouldDeleteUserFollowAndPublishEvent(){
        var follow = createFollow();
        assertEquals(1, userFollowRepository.count());
        assertDoesNotThrow(
            () -> userFollowService.removeFollow(follow.getId().userId(), follow.getId().followedUserId()));
        verify(
            userFollowEventProducer, 
            times(1)
        ).on(any());
        assertFalse(userFollowRepository
            .findById(UserFollowKey.create(follow.getId().userId(), follow.getId().followedUserId()))
            .isPresent()
        );
    }

    @Test
    void shouldReturnFollows(){
        var id = UUID.randomUUID();
        createFollow(id);
        createFollow(id);
        createFollow(id);
        assertEquals(3, userFollowService.getFollows(id, 0).getContent().size());
    }

    private UserFollow createFollow(){
        return userFollowRepository.save(UserFollow.create(UUID.randomUUID(), UUID.randomUUID()));
    }

    private UserFollow createFollow(UUID userId){
        return userFollowRepository.save(UserFollow.create(userId, UUID.randomUUID()));
    }
}
