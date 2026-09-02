package com.ambrosia.comment_service.attachment.service.impl;

import org.springframework.stereotype.Service;

import com.ambrosia.comment_service.attachment.model.dto.request.FileMetadata;
import com.ambrosia.comment_service.attachment.model.dto.response.AttachmentUploadResponse;
import com.ambrosia.comment_service.attachment.model.entity.CommentAttachment;
import com.ambrosia.comment_service.attachment.repository.AttachmentRepository;
import com.ambrosia.comment_service.attachment.service.AttachmentService;
import com.ambrosia.library_s3.utils.S3ConfigurationProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@RequiredArgsConstructor
@Slf4j
@Service
public class S3AttachmentServiceImpl implements AttachmentService{
    private final S3Presigner s3Presigner;

    private final S3Client s3Client;

    private final S3ConfigurationProperties configurationProperties;

    private final AttachmentRepository attachmentRepository;
    
    @Override
    public AttachmentUploadResponse attachMedia(String attachmentId, FileMetadata fileMetadata) {
        var tempKey = configurationProperties.getTempPrefix()+"/"+attachmentId;
        var url = s3Presigner
            .presignPutObject(t -> t
                .signatureDuration(configurationProperties.getSignatureDuration())
                .putObjectRequest(p -> p
                    .bucket(configurationProperties.getPublicBucket())
                    .checksumMD5(fileMetadata.md5())
                    .contentType(fileMetadata.contentType().getMimeType())
                    .contentLength(fileMetadata.fileSize())
                    .key(tempKey)
                    .build()
                )
                .build()
            )
            .url()
            .toString();
        return AttachmentUploadResponse.from(
            url,
            attachmentId
        );
    }

    @Override
    public void confirmAttachmentUpload(long commentId, String attachmentId) {
        var tempKey = configurationProperties.getTempPrefix()+"/"+attachmentId;
        var persitentKey = configurationProperties.getBasePrefix()+"/"+attachmentId;
        s3Client.copyObject(t -> t
            .sourceBucket(configurationProperties.getPublicBucket())
            .sourceKey(tempKey)
            .destinationBucket(configurationProperties.getPublicBucket())
            .destinationKey(persitentKey)
            .build()
        );
        attachmentRepository.save(CommentAttachment.builder()
            .commentId(commentId)
            .id(attachmentId)
            .isNew(true)
            .build()
        );
    }
}
