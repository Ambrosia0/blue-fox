package com.ambrosia.content_service.attachment.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ambrosia.content_service.attachment.repository.PostAttachmentRepository;
import com.ambrosia.content_service.attachment.service.DeletionWorker;
import com.ambrosia.library_s3.utils.S3ConfigurationProperties;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

@RequiredArgsConstructor
@Service
public class S3DeletionWorkerImpl implements DeletionWorker{
    private final PostAttachmentRepository postAttachmentRepository;

    private final S3Client s3Client;

    private final S3ConfigurationProperties configurationProperties;

    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.SECONDS)
    @Override
    public void deletePending() {
        var toDelete = postAttachmentRepository.findAllDeletable(configurationProperties.getDeleteBatchSize());
        if(toDelete.isEmpty())
            return;
        s3Client.deleteObjects(t -> t
            .bucket(configurationProperties.getPublicBucket())
            .delete(d -> d
                .objects(toDelete.stream().map(attach -> ObjectIdentifier.builder()
                        .key(
                            configurationProperties.getBasePrefix()+
                            "/"+
                            Long.toString(attach.getPostId())+
                            "/"+
                            attach.getAttachmentId()
                        )
                        .build()
                    )
                    .toList()
                )
                .build()
            )
            .build()
        );
        postAttachmentRepository.batchDeleteAll(toDelete);
    }
}
