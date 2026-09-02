package com.ambrosia.comment_service.core;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "app")
public class AppConfiguration{
    private float timeAffectionCoefficient = 0.7f;
    private URI fileEndpoint = URI.create("http://file-service/file/");
}
