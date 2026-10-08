package com.ambrosia.content_service.attachment.application.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ambrosia.content_service.attachment.api.dto.request.FileMetadata;
import com.ambrosia.content_service.attachment.api.dto.response.AttachmentUploadResponse;
import com.ambrosia.content_service.attachment.application.PostAttachmentUserService;
import com.ambrosia.content_service.attachment.domain.entity.PostAttachment;
import com.ambrosia.content_service.attachment.domain.repository.PostAttachmentRepository;
import com.ambrosia.content_service.attachment.utils.AttachmentIdGenerator;
import com.ambrosia.content_service.exception.api.AttachmentDoesntExistException;
import com.ambrosia.content_service.exception.api.NotEnoughPermissionsException;
import com.ambrosia.content_service.exception.internal.CantValidateAttachmentException;
import com.ambrosia.content_service.post.application.PostService;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_s3.utils.S3ConfigurationProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@RequiredArgsConstructor
@Slf4j
@Service
public class S3PostAttachmentUserServiceImpl implements PostAttachmentUserService{

    private final PostAttachmentRepository postAttachmentRepository;

    private final S3Client s3Client;

    private final S3Presigner s3Presigner;

    private final PostService postService;

    private final S3ConfigurationProperties configurationProperties;

    @Override
    public void deleteAttachment(Actor actor, long postId, String attachmentId) {
        if(postAttachmentRepository.deletionMark(actor.id(), postId, attachmentId) == 0)
            throw new AttachmentDoesntExistException("Editable attachment doesn't exist!");
    }

    @Override
    public AttachmentUploadResponse uploadAttachment(Actor actor, long postId, FileMetadata fileMetadata) {
        if(!postService.isAuthor(postId, actor.id()))
            throw new NotEnoughPermissionsException();
        var attachmentId = AttachmentIdGenerator.generateAttachmentId(postId);
        var key = configurationProperties.getTempPrefix()+"/"+postId+"/"+attachmentId;
        var url = s3Presigner.presignPutObject(t -> t
            .signatureDuration(configurationProperties.getSignatureDuration())
            .putObjectRequest(f -> f
                .bucket(configurationProperties.getPublicBucket())
                .checksumMD5(fileMetadata.md5())
                .contentLength(fileMetadata.fileSize())
                .contentType(fileMetadata.contentType().getMimeType())
                .key(key)
            )
        )
        .url()
        .toString();
        return AttachmentUploadResponse.from(url, attachmentId);
    }

    @Override
    public void validateAttachmentUpload(Actor actor, long postId, String attachmentId) {
        var sourceKey = configurationProperties.getTempPrefix()+"/"+postId+"/"+attachmentId;
        var destKey = configurationProperties.getBasePrefix()+"/"+postId+"/"+attachmentId;
        try {
            s3Client.copyObject(t -> t
                .sourceBucket(configurationProperties.getPublicBucket())
                .sourceKey(sourceKey)
                .destinationBucket(configurationProperties.getPublicBucket())
                .destinationKey(destKey)
                .build()
            );
        } catch (S3Exception e) {
            throw new CantValidateAttachmentException();
        } 
        try{
            postAttachmentRepository.save(PostAttachment.builder()
                .postId(postId)
                .attachmentId(attachmentId)
                .build()
            );
        } catch (RuntimeException e){
            try {
                s3Client.deleteObject(t -> t
                    .key(destKey)
                    .bucket(configurationProperties.getPublicBucket())
                    .build()
                );
            } catch (Exception ex) {
                e.addSuppressed(ex);
            }
            throw e;
        }
    }

    @Override
    public List<PostAttachment> getAttachments(Actor actor, long postId) {
        if(!postService.isAuthor(postId, actor.id()))
            throw new NotEnoughPermissionsException();
        return postAttachmentRepository.findByPostId(postId);
    }
}
