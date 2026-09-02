package com.ambrosia.community_service.follow.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Slice;

import com.ambrosia.community_service.follow.model.entity.CommunityFollow;

public interface CommunityFollowService {
    void followCommunity(long communityId, UUID requestingUser);
    void removeFollow(long communityId, UUID requestingUser);
    Slice<CommunityFollow> getFollows(UUID requestingUser, int page);
    List<UUID> getFollowedUsers(long communityId, int page);
}
