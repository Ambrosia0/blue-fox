package com.ambrosia.community_service.kafka.utils;

import java.util.UUID;

import com.ambrosia.community_service.kafka_events.CommunityFollowEvent;


public class CommunityFollowEventFactory {
    public static CommunityFollowEvent createFollow(UUID requestingUser, Long communityId){
        return CommunityFollowEvent.newBuilder()
            .setCommunityId(communityId)
            .setFollowed(true)
            .setRequestingUser(requestingUser.toString())
            .build();
    }

    public static CommunityFollowEvent createUnfollow(UUID requestingUser, Long communityId){
        return CommunityFollowEvent.newBuilder()
            .setCommunityId(communityId)
            .setRequestingUser(requestingUser.toString())
            .build();
    }
}
