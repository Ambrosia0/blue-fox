package com.ambrosia.content_service.integration;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.ambrosia.comment_service.kafka_events.CommentCreated;
import com.ambrosia.comment_service.kafka_events.CommentEvent;
import com.ambrosia.comment_service.kafka_events.User;
import com.ambrosia.content_service.BaseIntegrationTest;
import com.ambrosia.content_service.kafka.consumer.CommunityEventConsumer;
import com.ambrosia.content_service.post.repository.PostRepository;
import com.ambrosia.content_service.util.PostCreator;
import com.ambrosia.library_core.dto.Topics;

@TestPropertySource(properties = {
    "spring.kafka.streams.auto-startup=true",
})
@Import({PostCreator.class})
public class KafkaStreamsIntegrationTests extends BaseIntegrationTest{
    @Autowired KafkaTemplate<String, byte[]> kafkaTemplate;
    @Autowired PostRepository postRepository;
    @Autowired PostCreator postCreator;
    @MockitoSpyBean CommunityEventConsumer kafkaCommentCountEventConsumer;

    private final Duration awaitDuration = Duration.ofSeconds(40);

    @Test
    void shouldIncreaseCommentCountOnPostProjection(){
        var projection = postCreator.create(true);
        kafkaTemplate.send(Topics.COMMENT_EVENT, createCommentEvent(projection.getId()).toByteArray());
        await().pollInterval(Duration.ofSeconds(5)).atMost(awaitDuration).untilAsserted(
            () -> assertEquals(
                1, 
                postRepository.findById(projection.getId()).get().getCommentCount()
            )
        );
    }

    private CommentEvent createCommentEvent(Long postId){
        return CommentEvent.newBuilder()
            .setCommentId(ThreadLocalRandom.current().nextLong(1L, 999_999_999L))
            .setCreated(CommentCreated.newBuilder()
                .setContent("Test content")
                .setCreatedAt(Instant.now().toEpochMilli())
                .setPostId(postId)
                .setUser(User.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setFirstName("name")
                    .setLastName("name")
                    .setUsername("name")
                    .build()
                )
                .build()
            )
            .build();
    }

    @AfterAll
    void cleanUp(){
        postCreator.cleanUp();
    }
}
