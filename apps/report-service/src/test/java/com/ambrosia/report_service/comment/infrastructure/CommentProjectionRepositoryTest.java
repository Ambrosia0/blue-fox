package com.ambrosia.report_service.comment.infrastructure;

import com.ambrosia.comment_service.kafka_events.CommentCreated;
import com.ambrosia.comment_service.kafka_events.CommentDeleted;
import com.ambrosia.comment_service.kafka_events.CommentEvent;
import com.ambrosia.comment_service.kafka_events.User;
import com.ambrosia.report_service.BaseIntegrationTest;
import com.ambrosia.report_service.comment.repository.CommentProjectionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class CommentProjectionRepositoryTest extends BaseIntegrationTest {
    @Autowired
    CommentProjectionRepository commentProjectionRepository;
    
    @Test
    void shouldCreateCommentThenDeleteComment(){
        var event = createEvent();
        
        commentProjectionRepository.process(
            List.of(event),
            List.of()
        );
        
        Assertions.assertTrue(commentProjectionRepository.existsById(event.getCommentId()));
        
        commentProjectionRepository.process(
            List.of(),
            List.of(deleteEvent(event.getCommentId(), event.getPostId(), event.getVersion()))
        );
        
        Assertions.assertFalse(commentProjectionRepository.existsById(event.getCommentId()));
    }
    
    private CommentEvent createEvent(){
        return CommentEvent.newBuilder()
            .setEventId(UUID.randomUUID().toString())
            .setPostId(ThreadLocalRandom.current().nextLong())
            .setCommentId(ThreadLocalRandom.current().nextLong())
            .setVersion(0)
            .setCreated(CommentCreated.newBuilder()
                .setUser(User.newBuilder()
                    .setUsername("test")
                    .setFirstName("test")
                    .setLastName("test")
                    .setId(UUID.randomUUID().toString())
                    .build()
                )
                .build()
            )
            .build();
    }
    
    private CommentEvent deleteEvent(Long id, Long postId, Long version){
        return CommentEvent.newBuilder()
            .setEventId(UUID.randomUUID().toString())
            .setCommentId(id)
            .setPostId(postId)
            .setVersion(version)
            .setDeleted(CommentDeleted.newBuilder().build())
            .build();
    }
}
