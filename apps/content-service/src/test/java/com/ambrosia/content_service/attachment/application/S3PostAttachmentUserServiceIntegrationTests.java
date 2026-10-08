package com.ambrosia.content_service.attachment.application;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import com.ambrosia.content_service.BaseIntegrationTest;
import com.ambrosia.content_service.attachment.infrastructure.persistence.JdbcPostAttachmentRepository;
import com.ambrosia.content_service.attachment.utils.AttachmentIdGenerator;
import com.ambrosia.content_service.exception.api.NotEnoughPermissionsException;
import com.ambrosia.content_service.exception.internal.CantValidateAttachmentException;
import com.ambrosia.content_service.post.domain.repository.PostRepository;
import com.ambrosia.content_service.util.FileMetadataFactory;
import com.ambrosia.content_service.util.PostCreator;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

import software.amazon.awssdk.services.s3.S3Client;

@Import({
    PostCreator.class
})
public class S3PostAttachmentUserServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired PostAttachmentUserService postAttachmentUserService;
    @Autowired PostRepository postRepository;
    @Autowired JdbcPostAttachmentRepository postAttachmentRepository;
    @Autowired RestClient testRestClient;
    @Autowired S3Client s3Client;
    @Autowired PostCreator postCreator;

    @Test
    void shouldThrowNotEnoughPermissionsException(){
        var post = postCreator.create(false);
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> postAttachmentUserService.uploadAttachment(
                new Actor(UUID.randomUUID(), Role.USER), 
                post.getId(),
                FileMetadataFactory.fileMetadata()
            )
        );
    }

    @Test
    void shouldThrowCantValidateAttachmentUpload(){
        var post = postCreator.create(false);
        assertThrows(
            CantValidateAttachmentException.class,
            () -> postAttachmentUserService.validateAttachmentUpload(
                new Actor(post.getAuthorId(), Role.USER),
                post.getId(),
                AttachmentIdGenerator.generateAttachmentId(post.getId())
            )
        );
    }

    @Test
    void shouldUploadAttachmentThenDeleteAttachment() throws IOException{
        var post = postCreator.create(false);
        var file = FileMetadataFactory.fileMetadata();
        var resp = postAttachmentUserService.uploadAttachment(
                new Actor(post.getAuthorId(), Role.USER),
                post.getId(),
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
        assertDoesNotThrow(
            () -> postAttachmentUserService.validateAttachmentUpload(
                new Actor(post.getAuthorId(), Role.USER),
                post.getId(),
                resp.attachmentId()
            )
        );
        assertNotEquals(0, postAttachmentRepository.findByPostId(post.getId()).size());
        assertDoesNotThrow(() -> postAttachmentUserService.deleteAttachment(
            new Actor(post.getAuthorId(), Role.USER),
            post.getId(),
            resp.attachmentId()
        ));
        await()
            .pollInterval(Duration.ofSeconds(5))
            .atMost(Duration.ofSeconds(30))
            .untilAsserted(
                () -> assertEquals(0, postAttachmentRepository.findByPostId(post.getId()).size())
            );
    }

    @AfterEach
    void cleanUp(){
        postAttachmentRepository.deleteAll();
        postCreator.cleanUp();
    }
}
