package com.ambrosia.profile_service.core.utils;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppConfiguration {
    private Duration usernameChangeInterval = Duration.ofDays(28);

    private int maxOwnedCommunitiesPerUser = 3;

    public Duration getUsernameChangeInterval(){
        return this.usernameChangeInterval;
    }

    public Integer getMaxOwnedCommunitiesPerUser(){
        return this.maxOwnedCommunitiesPerUser;
    }

    public void setUsernameChangeInterval(Duration usernameChangeInterval){
        this.usernameChangeInterval = usernameChangeInterval;
    }

    public void setMaxOwnedCommunitiesPerUser(int maxOwned){
        this.maxOwnedCommunitiesPerUser = maxOwned;
    }
}
