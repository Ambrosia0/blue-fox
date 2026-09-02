package com.ambrosia.content_service.kafka.utils;

import java.util.UUID;

import com.ambrosia.content_service.kafka_events.UserFollowEvent;
import com.ambrosia.content_service.kafka_events.UserFollowed;
import com.ambrosia.content_service.kafka_events.UserUnfollowed;

public class UserFollowEventFactory {
    public static UserFollowEvent createFollow(UUID requestingUser, UUID followedUser){
        return UserFollowEvent.newBuilder()
            .setFollowed(UserFollowed.newBuilder()
                .setFollowedUserId(followedUser.toString())
                .setRequestingUser(requestingUser.toString())
                .build()
            )
            .build();
    }

    public static UserFollowEvent createUnfollow(UUID requestingUser, UUID followedUser){
        return UserFollowEvent.newBuilder()
            .setUnfollowed(UserUnfollowed.newBuilder()
                .setFollowedUserId(followedUser.toString())
                .setRequestingUser(requestingUser.toString())
                .build()
            )
            .build();
    }
}
