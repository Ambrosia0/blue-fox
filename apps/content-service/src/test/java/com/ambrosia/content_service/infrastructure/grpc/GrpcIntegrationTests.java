package com.ambrosia.content_service.infrastructure.grpc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import com.ambrosia.content_service.BaseIntegrationTest;
import com.ambrosia.content_service.grpc.CollaborationContentRequest;
import com.ambrosia.content_service.grpc.CollaborationSaveRequest;
import com.ambrosia.content_service.grpc.ContentServiceGrpc.ContentServiceBlockingStub;
import com.ambrosia.content_service.post.domain.repository.PostRepository;
import com.ambrosia.content_service.util.PostCreator;
import com.ambrosia.content_service.util.PostTemplate;

import io.grpc.StatusRuntimeException;

@TestPropertySource(properties = {"spring.grpc.client.default-channel.address=localhost:9090"})
public class GrpcIntegrationTests extends BaseIntegrationTest{
    @Autowired ContentServiceBlockingStub contentServiceBlockingStub;

    @Autowired PostCreator postCreator;

    @Autowired PostRepository postRepository;

    @Test 
    void shouldSavePost(){
        var post = postCreator.createCollaborationPost(false);
        contentServiceBlockingStub.saveCollaborationPost(
            createSaveRequest(post.getId())
        );
        var eps = 10;
        var updated = postRepository.findById(post.getId()).get();
        assertTrue(
            ChronoUnit.MICROS.between(
                post.getUpdatedAt().truncatedTo(ChronoUnit.MICROS),
                updated.getUpdatedAt().truncatedTo(ChronoUnit.MICROS)
            ) > eps
        );
    }

    @Test 
    void shouldReturnPost(){
        var post = postCreator.createCollaborationPost(false);
        var resp = contentServiceBlockingStub.getCollaborationPost(createContentRequest(post.getId()));
        assertEquals(post.getAuthorId().toString(), resp.getAuthor().getId());
    }

    @Test 
    void shouldThrowStatusExceptionUnexisingPost(){
        assertThrows(
            StatusRuntimeException.class,
            () -> contentServiceBlockingStub.getCollaborationPost(
                createContentRequest(ThreadLocalRandom.current().nextLong())
            )
        );
    }

    private CollaborationContentRequest createContentRequest(Long postId){
        return CollaborationContentRequest.newBuilder()
            .setPostId(postId)
            .build();
    }

    private CollaborationSaveRequest createSaveRequest(Long postId){
        return CollaborationSaveRequest.newBuilder()
            .setPostId(postId)
            .setContent(PostTemplate.template)
            .build();
    }
}
