package com.ambrosia.profile_service.core.service.impl;

import java.time.Duration;
 import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.library_s3.utils.S3ConfigurationProperties;
import com.ambrosia.profile_service.core.service.AvatarService;
import com.ambrosia.profile_service.user.model.dto.request.FileMetadata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@RequiredArgsConstructor
@Slf4j
@Service
public class S3AvatarServiceImpl implements AvatarService{
    private final S3Presigner s3Presigner;

    private final S3Client s3Client;

    private final S3ConfigurationProperties configurationProperties;

    @Override
    public String upload(UUID userId, String avatarId, FileMetadata fileMetadata) {
        return s3Presigner.presignPutObject(
            PutObjectPresignRequest.builder()
                .putObjectRequest(t -> t
                    .key(configurationProperties.getTempPrefix()+"/"+userId.toString()+"/"+avatarId)
                    .contentLength(fileMetadata.fileSize())
                    .contentType(fileMetadata.contentType().getMimeType())
                    .checksumMD5(fileMetadata.md5())
                    .bucket(configurationProperties.getPublicBucket())
                )
                .signatureDuration(Duration.ofMinutes(5))
                .build()
        )
        .url()
        .toString();
    }

    @Override
    public boolean validateUpload(UUID userId, String avatarId) {
        try {
            var tempKey = configurationProperties.getTempPrefix()+"/"+userId.toString()+"/"+avatarId;
            var persistentKey = configurationProperties.getBasePrefix()+"/"+userId.toString()+"/"+avatarId;
            s3Client.copyObject(t -> t
                .sourceKey(tempKey)
                .sourceBucket(configurationProperties.getPublicBucket())
                .destinationBucket(configurationProperties.getPublicBucket())
                .destinationKey(persistentKey)
            );
            return true;
        } catch (S3Exception e) {
            return false;
        }
    }

    @Override
    public void delete(UUID userId, String avatarId) {
        s3Client.deleteObject(t -> t
            .key(configurationProperties.getBasePrefix()+"/"+userId.toString()+"/"+avatarId)
            .bucket(configurationProperties.getPublicBucket())
            .build()
        );
    }
}
