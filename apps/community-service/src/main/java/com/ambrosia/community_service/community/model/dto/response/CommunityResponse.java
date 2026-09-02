package com.ambrosia.community_service.community.model.dto.response;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import com.ambrosia.community_service.community.model.entity.Community;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CommunityResponse implements Serializable {
    private long id;
    private String slug;
    private String displayedName;

    private UUID ownerId;

    private String avatarId;
    
    private String description;

    private String[] rules;

    private String[] tags;

    private UUID[] communityModerators;

    private long postCount;

    private long followCount;

    private boolean isPrivate;


    @JsonInclude(value = Include.NON_NULL)
    @JsonUnwrapped
    private CommunityUserData communityUserData;

    // @JsonInclude(value = Include.NON_NULL)
    // Boolean isFollowed;

    // List<ScopeEnum> scopes;

    private Instant createdAt;

    public static CommunityResponse create(Community community){
        return new CommunityResponse(
            community.getId(),
            community.getSlug(),
            community.getDisplayedName(), 
            community.getOwnerId(), 
            community.getAvatarId(),
            community.getDescription(),
            community.getRules() != null?
                community.getRules().toArray(String[]::new):
                null,
            community.getTags() != null?
                community.getTags().toArray(String[]::new):
                null,
            null,
            community.getPostCount(),
            community.getFollowCount(),
            community.isPrivate(),
            null,
            community.getCreatedAt());
    }
}
