package com.ambrosia.community_service.utils;

import java.util.List;
import java.util.UUID;

import com.ambrosia.community_service.community.model.dto.request.CommunityEdit;

public class CommunityEditBuilder {
    private List<String> tags;
    private List<String> rules;
    private String displayedName;
    private String description;
    private UUID ownerId;

    public static CommunityEditBuilder builder(){
        return new CommunityEditBuilder();
    }

    public CommunityEditBuilder setTags(List<String> tags){
        this.tags = tags;
        return this;
    }

    public CommunityEditBuilder setRules(List<String> rules){
        this.rules = rules;
        return this;
    }

    public CommunityEditBuilder setDisplayedName(String displayedName){
        this.displayedName = displayedName;
        return this;
    }

    public CommunityEditBuilder setDescription(String description){
        this.description = description;
        return this;
    }

    public CommunityEditBuilder setOwnerId(UUID ownerId){
        this.ownerId = ownerId;
        return this;
    }

    public CommunityEdit build(){
        return new CommunityEdit(tags, rules, displayedName, description, ownerId);
    }
}
