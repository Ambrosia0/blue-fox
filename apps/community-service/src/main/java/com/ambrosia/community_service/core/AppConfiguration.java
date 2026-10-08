package com.ambrosia.community_service.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppConfiguration {

    private int maxOwnedCommunitiesPerUser = 3;

    public Integer getMaxOwnedCommunitiesPerUser(){
        return this.maxOwnedCommunitiesPerUser;
    }

    public void setMaxOwnedCommunitiesPerUser(int maxOwned){
        this.maxOwnedCommunitiesPerUser = maxOwned;
    }
}