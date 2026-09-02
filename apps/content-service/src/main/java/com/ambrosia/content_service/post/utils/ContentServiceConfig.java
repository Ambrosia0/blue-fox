package com.ambrosia.content_service.post.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "app", ignoreUnknownFields = true)
@Getter
@Setter
public class ContentServiceConfig {
    // limit of images on preview
    private int previewImageLimit = 1;

    // limit of characters in preview
    private int previewCharLimit = 200;
}
