package com.ambrosia.comment_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.ambrosia.comment_service.core.AppConfiguration;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(AppConfiguration.class)
public class CommentApplication{
    public static void main(String[] args) {
        SpringApplication.run(CommentApplication.class, args);
    }
}