package com.ambrosia.profile_service.infrastructure;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.ambrosia.notification_service.kafka_events.ActivityEvent;
import org.ambrosia.notification_service.kafka_events.ActivityEventType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;

import com.ambrosia.content_service.kafka_events.UserFollowEvent;
import com.ambrosia.content_service.kafka_events.UserFollowed;
import com.ambrosia.content_service.kafka_events.UserUnfollowed;
import com.ambrosia.library_core.dto.Topics;
import com.ambrosia.profile_service.BaseIntegrationTest;
import com.ambrosia.profile_service.user.infrastructure.persistence.JdbcUserRepository;
import com.ambrosia.profile_service.user.utils.Status;
import com.ambrosia.profile_service.util.UserCreator;

@TestPropertySource(properties = {
    "spring.kafka.streams.auto-startup=true",
})
public class KafkaStreamAggregationIntegrationTests extends BaseIntegrationTest{
    @Autowired KafkaTemplate<String, byte[]> kafkaTemplate;
    @Autowired JdbcUserRepository userRepository;

    private final Duration awaitDuration = Duration.ofSeconds(30);

    @Autowired UserCreator userCreator;

    @Test
    void shouldAggregateFollowCountOnUser() throws Exception{
        var user = userCreator.create();
        var event1 = createUserFollowEvent(user.getId());
        kafkaTemplate.send(
            Topics.USER_FOLLOW_EVENT,
            event1.getFollowed().getFollowedUserId().toString(),
            event1.toByteArray()
        );
        
        await().pollInterval(Duration.ofSeconds(2)).atMost(awaitDuration).untilAsserted(
            () -> assertNotEquals(0L, userRepository.findById(user.getId()).get().getFollowCount().longValue())
        );
        
        var event2 = createUserUnfollowEvent(user.getId());
        kafkaTemplate.send(
            Topics.USER_FOLLOW_EVENT,
            event2.getUnfollowed().getFollowedUserId().toString(),
            event2.toByteArray()
        );
        await().pollInterval(Duration.ofSeconds(2)).atMost(awaitDuration).untilAsserted(
            () -> assertEquals(0L, userRepository.findById(user.getId()).get().getFollowCount().longValue())
        );
    }

    @Test
    void shouldChangePresenseInfoForUser(){
        var user = userCreator.create();
        var event = createActivityEvent(user.getId(), ActivityEventType.CONNECT);
        kafkaTemplate.send(
            Topics.USER_ACTIVITY, 
            event.getUserId(), 
            event.toByteArray()
        );
        await().pollInterval(Duration.ofSeconds(2)).atMost(awaitDuration).untilAsserted(
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

}
