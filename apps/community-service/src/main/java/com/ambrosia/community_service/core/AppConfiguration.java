package com.ambrosia.community_service.core;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppConfiguration {

    private int maxOwnedCommunitiesPerUser = 3;

    private URI fileServiceEndpoint = URI.create("http://file-service/api/content");

    public Integer getMaxOwnedCommunitiesPerUser(){
        return this.maxOwnedCommunitiesPerUser;
    }

    public URI getFileServiceEndpoint(){
        return this.fileServiceEndpoint;
    }

    public void setFileServiceEndpoint(URI fileServiceEndpoint){
        this.fileServiceEndpoint = fileServiceEndpoint;
    }
    
    public void setMaxOwnedCommunitiesPerUser(int maxOwned){
        this.maxOwnedCommunitiesPerUser = maxOwned;
    }
}