package com.ambrosia.content_service.follow.application;

import java.util.UUID;

import org.springframework.data.domain.Slice;

import com.ambrosia.content_service.follow.api.dto.UserFollowResponse;

public interface UserFollowService {
    void followUser(UUID requestingUser, UUID followedUser);
    void removeFollow(UUID requestingUser, UUID followedUser);
    Slice<UserFollowResponse> getFollows(UUID requestingUser, int page);
}
