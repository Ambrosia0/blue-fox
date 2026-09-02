package com.ambrosia.content_service.follow.model.entity.keys;

import java.util.UUID;

import org.springframework.data.relational.core.mapping.Column;

public record UserFollowKey(
    @Column(value = "user_id") UUID userId,
    @Column(value = "followed_user_id") UUID followedUserId
) {
    public static UserFollowKey create(UUID userId, UUID followedUserId){
        return new UserFollowKey(userId, followedUserId);
    }
}
