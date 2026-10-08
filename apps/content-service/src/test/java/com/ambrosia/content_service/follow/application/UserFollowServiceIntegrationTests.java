package com.ambrosia.content_service.follow.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.content_service.BaseIntegrationTest;
import com.ambrosia.content_service.exception.api.AlreadyFollowedException;
import com.ambrosia.content_service.exception.api.DoesntFollowedException;
import com.ambrosia.content_service.exception.api.UserDoesntExistException;
import com.ambrosia.content_service.follow.domain.repository.UserFollowRepository;
import com.ambrosia.content_service.follow.infrastructure.entity.UserFollow;
import com.ambrosia.content_service.follow.infrastructure.entity.keys.UserFollowKey;
import com.ambrosia.content_service.infrastructure.kafka.producer.UserFollowEventProducer;
import com.ambrosia.content_service.util.UserCreator;

@Import({UserCreator.class})
@Transactional
public class UserFollowServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired UserFollowService userFollowService;
    @MockitoSpyBean UserFollowEventProducer userFollowEventProducer;

    @Autowired UserFollowRepository userFollowRepository;
    @Autowired UserCreator userCreator;

    @Test
    void shouldThrowUserDoesntExistException(){
        var user = userCreator.create();
        assertThrows(
            UserDoesntExistException.class, 
            () -> userFollowService.followUser(user.getId(), UUID.randomUUID()));
    }

    @Test
    void shouldThrowAlreadyFollowedException(){
        var user = userCreator.create();
        var followedUser = userCreator.create();
        var follow = createFollow(user.getId(), followedUser.getId());
        assertThrows(
            AlreadyFollowedException.class, 
            () -> userFollowService.followUser(follow.getId().userId(), follow.getId().followedUserId()));
    }

    @Test
    void shouldCreateUserFollowAndPublishEvent(){
        var user = userCreator.create();
        var followed = userCreator.create();
        assertDoesNotThrow(() -> userFollowService.followUser(user.getId(), followed.getId()));
        verify(
            userFollowEventProducer,
            times(1)
        ).on(any());
        assertTrue(userFollowRepository
                .findById(UserFollowKey.create(user.getId(), followed.getId()))
                .isPresent()
        );
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
        var user = userCreator.create();
        var followed = userCreator.create();
        var follow = createFollow(user.getId(), followed.getId());
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
        var user = userCreator.create();
        createFollow(user.getId(), userCreator.create().getId());
        createFollow(user.getId(), userCreator.create().getId());
        createFollow(user.getId(), userCreator.create().getId());
        assertEquals(3, userFollowService.getFollows(user.getId(), 0).getContent().size());
    }

    private UserFollow createFollow(UUID userId, UUID followedUserId){
        return userFollowRepository.save(UserFollow.create(userId, followedUserId));
    }
}
