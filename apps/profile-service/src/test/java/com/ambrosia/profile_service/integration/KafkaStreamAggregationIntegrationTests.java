package com.ambrosia.profile_service.integration;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.ambrosia.notification_service.kafka_events.ActivityEvent;
import org.ambrosia.notification_service.kafka_events.ActivityEventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.ambrosia.content_service.kafka_events.UserFollowEvent;
import com.ambrosia.content_service.kafka_events.UserFollowed;
import com.ambrosia.content_service.kafka_events.UserUnfollowed;
import com.ambrosia.library_core.dto.Topics;
import com.ambrosia.profile_service.BaseIntegrationTest;
import com.ambrosia.profile_service.kafka.consumer.KafkaUserFollowAggregation;
import com.ambrosia.profile_service.user.model.entity.User;
import com.ambrosia.profile_service.user.repository.UserRepository;
import com.ambrosia.profile_service.user.utils.Status;
import com.ambrosia.profile_service.util.Factory;

@TestPropertySource(properties = {
    "spring.kafka.streams.auto-startup=true",
})
public class KafkaStreamAggregationIntegrationTests extends BaseIntegrationTest{
    @Autowired KafkaTemplate<String, byte[]> kafkaTemplate;
    @Autowired UserRepository userRepository;
    @MockitoSpyBean KafkaUserFollowAggregation kafkaUserFollowAggregation;

    private final Duration awaitDuration = Duration.ofSeconds(60);

    @AfterEach
    void cleanUp(){
        userRepository.deleteAll();
    }

    @Test
    void shouldAggregateFollowCountOnUser() throws Exception{
        var user = createUser();
        var event1 = createUserFollowEvent(user.getId());
        kafkaTemplate.send(
            Topics.USER_FOLLOW_EVENT,
            event1.getFollowed().getFollowedUserId().toString(),
            event1.toByteArray()
        );
        
        await().pollInterval(Duration.ofSeconds(5)).atMost(awaitDuration).untilAsserted(
            () -> assertNotEquals(0L, userRepository.findById(user.getId()).get().getFollowCount().longValue())
        );
        
        var event2 = createUserUnfollowEvent(user.getId());
        kafkaTemplate.send(
            Topics.USER_FOLLOW_EVENT,
            event2.getUnfollowed().getFollowedUserId().toString(),
            event2.toByteArray()
        );
        await().pollInterval(Duration.ofSeconds(5)).atMost(awaitDuration).untilAsserted(
            () -> assertEquals(0L, userRepository.findById(user.getId()).get().getFollowCount().longValue())
        );
    }

    @Test
    void shouldChangePresenseInfoForUser(){
        var user = createUser();
        var event = createActivityEvent(user.getId(), ActivityEventType.CONNECT);
        kafkaTemplate.send(
            Topics.USER_ACTIVITY, 
            event.getUserId(), 
            event.toByteArray()
        );
        await().pollInterval(Duration.ofSeconds(5)).atMost(awaitDuration).untilAsserted(
            () -> assertEquals(Status.ONLINE, userRepository.findById(user.getId()).get().getStatus())
        );
    }

    private UserFollowEvent createUserFollowEvent(UUID followedUser){
        return UserFollowEvent.newBuilder()
            .setFollowed(UserFollowed.newBuilder()
                .setFollowedUserId(followedUser.toString())
                .setRequestingUser(UUID.randomUUID().toString())
                .build()
            )
            .build();
    }

    private UserFollowEvent createUserUnfollowEvent(UUID followedUser){
        return UserFollowEvent.newBuilder()
            .setUnfollowed(UserUnfollowed.newBuilder()
                .setFollowedUserId(followedUser.toString())
                .setRequestingUser(UUID.randomUUID().toString())
                .build()
            )
            .build();
    }

    private ActivityEvent createActivityEvent(UUID userId, ActivityEventType type){
        return ActivityEvent.newBuilder()
            .setUserId(userId.toString())
            .setTimestamp(Instant.now().toEpochMilli())
            .setType(type)
            .build();
    }

    private User createUser(){
        return userRepository.save(
            Factory.createUser()
        );
    }
}
