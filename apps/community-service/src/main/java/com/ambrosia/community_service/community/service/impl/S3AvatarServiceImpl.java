package com.ambrosia.community_service.community.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.community_service.community.model.dto.request.FileMetadata;
import com.ambrosia.community_service.community.service.AvatarService;
import com.ambrosia.library_s3.utils.S3ConfigurationProperties;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Slf4j
@RequiredArgsConstructor
@CircuitBreaker(name = "s3-store", fallbackMethod = "fallback")
@Service
public class S3AvatarServiceImpl implements AvatarService{
    private final S3ConfigurationProperties configurationProperties;

    private final S3Client s3Client;

    private final S3Presigner s3Presigner;

    @Override
    public String upload(Long communityId, String avatarId, FileMetadata fileMetadata) {
        var key = configurationProperties.getTempPrefix()+"/"+communityId+"/"+avatarId;
        return s3Presigner.presignPutObject(t -> t
            .signatureDuration(configurationProperties.getSignatureDuration())
            .putObjectRequest(r -> r
                .bucket(configurationProperties.getPublicBucket())
                .key(key)
                .checksumMD5(fileMetadata.md5())
                .contentType(fileMetadata.contentType().getMimeType())
                .contentLength(fileMetadata.fileSize())
                .build()
            )
            .build()
        )
        .url()
        .toString();
    }

    @Override
    public void delete(Long communityId, String avatarId) {
        var key = configurationProperties.getBasePrefix()+"/"+communityId+"/"+avatarId;
        s3Client.deleteObject(t -> t
            .bucket(configurationProperties.getPublicBucket())
            .key(key)
        );
    }

    @Override
    public void confirmUpload(Long communityId, String avatarId) {
        var tempKey = configurationProperties.getTempPrefix()+"/"+communityId+"/"+avatarId;
        var key = configurationProperties.getBasePrefix()+"/"+communityId+"/"+avatarId;
        s3Client.copyObject(t -> t
            .sourceBucket(configurationProperties.getPublicBucket())
            .sourceKey(tempKey)
            .destinationBucket(configurationProperties.getPublicBucket())
            .destinationKey(key)
            .build()
        );
    }

    @SuppressWarnings("unused")
    private String fallback(UUID userId, FileMetadata fileMetadata, RuntimeException e){
        throw e;
    }
}
