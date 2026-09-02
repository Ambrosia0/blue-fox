package com.ambrosia.content_service.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "app.elastic")
public class ElasticConfig {
    private boolean enabled;
    private boolean ssl;
    private String host;
    private String username;
    private String password;
}
