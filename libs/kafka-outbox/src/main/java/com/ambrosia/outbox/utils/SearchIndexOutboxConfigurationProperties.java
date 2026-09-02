package com.ambrosia.outbox.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.outbox.elasticsearch")
public class SearchIndexOutboxConfigurationProperties {
    long pollInterval = 5000;
}
