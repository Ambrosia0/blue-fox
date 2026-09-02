package com.ambrosia.community_service.integration;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.community.model.entity.Community;
import com.ambrosia.community_service.community.repository.CommunityRepository;
import com.ambrosia.community_service.kafka.consumer.KafkaCommunityFollowAggregation;
import com.ambrosia.community_service.kafka.producer.CommunityFollowEventProducer;
import com.ambrosia.community_service.kafka_events.CommunityFollowEvent;
import com.ambrosia.community_service.utils.Factory;
import com.ambrosia.content_service.kafka_events.PostCreated;
import com.ambrosia.content_service.kafka_events.PostDeleted;
import com.ambrosia.content_service.kafka_events.PostEvent;
import com.ambrosia.library_core.dto.Topics;


@TestPropertySource(properties = {
    "spring.kafka.streams.auto-startup=true",
})
public class KafkaStreamAggregationIntegrationTests extends BaseIntegrationTest{
    @Autowired KafkaTemplate<String, byte[]> kafkaTemplate;
    @Autowired CommunityFollowEventProducer communityFollowEventProducer;
    @Autowired CommunityRepository communityRepository; 
    @MockitoSpyBean KafkaCommunityFollowAggregation kafkaCommunityFollowAggregation;

    private final Duration awaitDuration = Duration.ofMinutes(2);

    @AfterEach
    void cleanUp(){
        communityRepository.deleteAll();
    }

    @Test
    void shouldAggregateFollowCountOnCommunity(){
        var community = createCommunity();
        var event1 = createCommunityFollowEvent(community.getId());
        communityFollowEventProducer.on(event1);
        await().pollInterval(Duration.ofSeconds(5)).atMost(awaitDuration).untilAsserted(
            () -> assertNotEquals(
                0L, 
                communityRepository.findById(community.getId()).get().getFollowCount().longValue()
            )
        );

        var event2 = createCommunityUnfollowEvent(community.getId());
        communityFollowEventProducer.on(event2);
        await().pollInterval(Duration.ofSeconds(5)).atMost(awaitDuration).untilAsserted(
            () -> assertEquals(
                0L, 
                communityRepository.findById(community.getId()).get().getFollowCount().longValue())
        );
    }

    @Test
    void shouldAggregatePostCountOnCommunity(){
        var community = createCommunity();
        var event1 = createPostCreateEvent(community.getId());
        var postId1 = event1.getCreated().getId();
        kafkaTemplate.send(
            Topics.POST_EVENT,
            Long.toString(postId1),
            event1.toByteArray()
        ).join();

        await().pollInterval(Duration.ofSeconds(5)).atMost(awaitDuration).untilAsserted(
            () -> assertNotEquals(
                0L, 
                communityRepository.findById(community.getId()).get().getPostCount().longValue()
            )
        );

        var event2 = createPostDeleteEvent(community.getId());
        var postId2 = event2.getDeleted().getId();
        kafkaTemplate.send(
            Topics.POST_EVENT,     
            Long.toString(postId2),
            event2.toByteArray()
        ).join();
        
        await().pollInterval(Duration.ofSeconds(5)).atMost(awaitDuration).untilAsserted(
            () -> assertEquals(
                0L, 
                communityRepository.findById(community.getId()).get().getPostCount().longValue())
        );
    }

    private CommunityFollowEvent createCommunityFollowEvent(long communityId){
        return CommunityFollowEvent.newBuilder()
            .setCommunityId(communityId)
            .setRequestingUser(UUID.randomUUID().toString())
            .setFollowed(true)
            .build();
    }

    private PostEvent createPostCreateEvent(long communityId){
        return PostEvent.newBuilder()
            .setCreated(PostCreated.newBuilder()
                .setAuthorId(UUID.randomUUID().toString())
                .setCommunityId(communityId)
                .setTitle("TestTitle")
                .setPublishedAt(Instant.now().toEpochMilli())
                .setId(ThreadLocalRandom.current().nextLong(1, 999_999_999_999L))
                .setPreview("test")
                .build()
            )
            .build();
    }

    private PostEvent createPostDeleteEvent(long communityId){
        return PostEvent.newBuilder()
            .setDeleted(PostDeleted.newBuilder()
                .setCommunityId(communityId)
                .setId(ThreadLocalRandom.current().nextLong(1, 999_999_999_999L))
                .build()
            )
            .build();
    }

    private CommunityFollowEvent createCommunityUnfollowEvent(long communityId){
        return CommunityFollowEvent.newBuilder()
            .setCommunityId(communityId)
            .setRequestingUser(UUID.randomUUID().toString())
            .setFollowed(false)
            .build();
    }

    private Community createCommunity(){
        var userId = UUID.randomUUID();
        var community = Factory.createCommunity("TestCommunity", userId);
        return communityRepository.save(
            community
        );
    }
}
