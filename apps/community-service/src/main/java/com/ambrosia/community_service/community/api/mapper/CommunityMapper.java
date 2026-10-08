package com.ambrosia.community_service.community.api.mapper;

import org.springframework.stereotype.Component;

import com.ambrosia.community_service.community.api.dto.response.CommunityCreateResponse;
import com.ambrosia.community_service.community.api.dto.response.CommunityEditResponse;
import com.ambrosia.community_service.community.domain.entity.Community;

@Component 
public class CommunityMapper {
    public CommunityCreateResponse toCreateResponse(Community community){
        return new CommunityCreateResponse(community.getId());
    }

    public CommunityEditResponse toEditResponse(Community community){
        return new CommunityEditResponse(
            community.getDisplayedName(),
            community.getDescription(),
            community.isPrivate(),
            community.getRules(),
            community.getTags(),
            community.getOwnerId(),
            community.getVersion()
        );
    }
}
