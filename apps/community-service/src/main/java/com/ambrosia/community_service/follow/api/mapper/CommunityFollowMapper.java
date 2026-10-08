package com.ambrosia.community_service.follow.api.mapper;

import org.springframework.stereotype.Component;

import com.ambrosia.community_service.follow.api.dto.response.CommunityFollowCreateResponse;
import com.ambrosia.community_service.follow.api.dto.response.CommunityFollowCreateResponse.Type;
import com.ambrosia.community_service.follow.domain.entity.CommunityFollow;
import com.ambrosia.community_service.follow.domain.entity.CommunityFollow.FollowState;

@Component 
public class CommunityFollowMapper {
    public CommunityFollowCreateResponse toResponse(CommunityFollow communityFollow){
        return new CommunityFollowCreateResponse(
            communityFollow.getState() == FollowState.FOLLOWED?
                Type.FOLLOWED:
                Type.REQUESTED
        );
    }
}
