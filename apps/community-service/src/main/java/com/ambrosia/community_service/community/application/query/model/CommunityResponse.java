package com.ambrosia.community_service.community.application.query.model;

import java.io.Serializable;
import java.time.Instant;

import com.ambrosia.community_service.user.model.entity.UserResponse;
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

    private UserResponse user;

    private String avatarId;
    
    private String description;

    private String[] rules;

    private String[] tags;

    private UserResponse[] communityModerators;

    private long postCount;

    private long followCount;

    private boolean isPrivate;


    @JsonInclude(value = Include.NON_NULL)
    @JsonUnwrapped
    private CommunityUserDataResponse communityUserData;

    // @JsonInclude(value = Include.NON_NULL)
    // Boolean isFollowed;

    // List<ScopeEnum> scopes;

    private Instant createdAt;

}
