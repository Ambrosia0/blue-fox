package com.ambrosia.content_service.follow.service;

import java.util.UUID;

import org.springframework.data.domain.Slice;

import com.ambrosia.content_service.follow.model.dto.UserFollowResponse;

public interface UserFollowService {
    void followUser(UUID requestingUser, UUID followedUser);
    void removeFollow(UUID requestingUser, UUID followedUser);
    Slice<UserFollowResponse> getFollows(UUID requestingUser, int page);
}
