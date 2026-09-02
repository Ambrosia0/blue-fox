package com.ambrosia.community_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.ambrosia.community_service.core.AppConfiguration;

@SpringBootApplication
@EnableConfigurationProperties(AppConfiguration.class)
public class CommunityApplication {
    public static final void main(String[] args){
        SpringApplication.run(CommunityApplication.class, args);
    }    
}
