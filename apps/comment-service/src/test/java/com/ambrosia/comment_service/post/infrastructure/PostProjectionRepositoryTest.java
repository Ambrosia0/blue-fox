package com.ambrosia.comment_service.post.infrastructure;

import com.ambrosia.comment_service.BaseIntegrationTest;
import com.ambrosia.comment_service.post.repository.PostProjectionRepository;
import com.ambrosia.content_service.grpc.User;
import com.ambrosia.content_service.kafka_events.PostCreated;
import com.ambrosia.content_service.kafka_events.PostDeleted;
import com.ambrosia.content_service.kafka_events.PostEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PostProjectionRepositoryTest extends BaseIntegrationTest {
    @Autowired
    PostProjectionRepository postProjectionRepository;
    
    @Test
    void shouldCreatePostThenDeletePost(){
        var create = createEvent();
        
        postProjectionRepository.process(
            List.of(create),
            List.of()
        );
        
        Assertions.assertTrue(postProjectionRepository.existsById(create.getPostId()));
        
        postProjectionRepository.process(
            List.of(),
            List.of(deleteEvent(create.getPostId(), create.getVersion()))
        );
        
        Assertions.assertTrue(
            postProjectionRepository.findById(create.getPostId())
                .isEmpty()
        );
    }
    
    private PostEvent createEvent(){
        return PostEvent.newBuilder()
            .setPostId(ThreadLocalRandom.current().nextLong())
            .setEventId(UUID.randomUUID().toString())
            .setVersion(0)
            .setCreated(PostCreated.newBuilder()
                .setAuthor(User.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setUsername("test")
                    .setFirstName("test")
                    .setLastName("test")
                    .build()
                )
                .setTitle("test")
                .setPreview("test")
                .setPublishedAt(Instant.now().toEpochMilli())
                .build()
            )
            .build();
    }
    
    private PostEvent deleteEvent(Long id, Long version){
        return PostEvent.newBuilder()
            .setPostId(id)
            .setEventId(UUID.randomUUID().toString())
            .setVersion(version)
            .setDeleted(PostDeleted.newBuilder()
                .build())
            .build();
    }
}
