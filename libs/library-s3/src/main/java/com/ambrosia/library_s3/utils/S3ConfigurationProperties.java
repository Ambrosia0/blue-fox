package com.ambrosia.library_s3.utils;

import java.net.URI;
import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app.s3")
public class S3ConfigurationProperties {
    /**
     * Bucket where is stored files
     */
    @NotNull
    private String publicBucket = "publicbucket";

    /**
     * Prefix for post media files
     */
    @NotNull
    private String basePrefix = "";

    /**
     * Prefix for temporary files
     */
    @NotNull
    private String tempPrefix = "";

    /**
     * Batch size of deleted attachments
     */
    @Min(1)
    private int deleteBatchSize = 300;

    /**
     * Signature duration for presigned urls
     */
    @NotNull
    private Duration signatureDuration = Duration.ofMinutes(10);
}
