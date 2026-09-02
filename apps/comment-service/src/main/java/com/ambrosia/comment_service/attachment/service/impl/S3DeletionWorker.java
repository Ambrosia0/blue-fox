package com.ambrosia.comment_service.attachment.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ambrosia.comment_service.attachment.repository.AttachmentRepository;
import com.ambrosia.comment_service.attachment.service.DeletionWorker;
import com.ambrosia.library_s3.utils.S3ConfigurationProperties;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

@RequiredArgsConstructor
@Service
public class S3DeletionWorker implements DeletionWorker{
    private final AttachmentRepository attachmentRepository;

    private final S3ConfigurationProperties configurationProperties;

    private final S3Client s3Client;

    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.SECONDS)
    @Override
    public void deletePending() {
        var toDelete = attachmentRepository.findAllDeletable(configurationProperties.getDeleteBatchSize());
        if(toDelete.isEmpty())
            return;
        s3Client.deleteObjects(t -> t
            .bucket(configurationProperties.getPublicBucket())
            .delete(d -> d
                .objects(
                    toDelete.stream().map(attach -> ObjectIdentifier
                        .builder()
                        .key(
                            configurationProperties.getBasePrefix()+
                            "/"+
                            attach.getId()
                        )
                        .build()
                    )
                    .toList()
                )
            )
        );
        attachmentRepository.batchDelete(toDelete);
    }    
}
