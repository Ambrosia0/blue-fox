package com.ambrosia.content_service.attachment.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.content_service.attachment.model.dto.request.FileMetadata;
import com.ambrosia.content_service.attachment.model.dto.response.AttachmentUploadResponse;
import com.ambrosia.content_service.attachment.model.entity.PostAttachment;
import com.ambrosia.content_service.attachment.repository.PostAttachmentRepository;
import com.ambrosia.content_service.attachment.service.PostAttachmentUserService;
import com.ambrosia.content_service.attachment.utils.AttachmentIdGenerator;
import com.ambrosia.content_service.exception.api.AttachmentDoesntExistException;
import com.ambrosia.content_service.exception.api.NotEnoughPermissionsException;
import com.ambrosia.content_service.exception.internal.CantValidateAttachmentException;
import com.ambrosia.content_service.post.service.user.PostUserService;
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

    private final PostUserService postUserService;

    private final S3ConfigurationProperties configurationProperties;

    @Override
    public void deleteAttachment(UUID requestingUser, long postId, String attachmentId) {
        if(postAttachmentRepository.deletionMark(requestingUser, postId, attachmentId) == 0)
            throw new AttachmentDoesntExistException("Editable attachment doesn't exist!");
    }

    @Override
    public AttachmentUploadResponse uploadAttachment(UUID userId, long postId, FileMetadata fileMetadata) {
        if(!postUserService.isAuthor(postId, userId))
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
    public void validateAttachmentUpload(UUID userId, long postId, String attachmentId) {
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
    public List<PostAttachment> getAttachments(UUID requestingUser, long postId) {
        if(!postUserService.isAuthor(postId, requestingUser))
            throw new NotEnoughPermissionsException();
        return postAttachmentRepository.findByPostId(postId);
    }
}
