package com.ambrosia.community_service.kafka.utils;

import java.util.UUID;

import com.ambrosia.community_service.kafka_events.CommunityBan;
import com.ambrosia.community_service.kafka_events.CommunityBanEvent;
import com.ambrosia.community_service.kafka_events.CommunityUnban;

public class CommunityBanEventFactory {
    public static CommunityBanEvent createBan(Long communityId, UUID userId){
        return CommunityBanEvent.newBuilder()
            .setBan(CommunityBan.newBuilder()
                .setCommunityId(communityId)
                .setUserId(userId.toString())
                .build()
            )
            .build();

    }

    public static CommunityBanEvent createUnban(Long communityId, UUID userId){
        return CommunityBanEvent.newBuilder()
            .setUnban(CommunityUnban.newBuilder()
                .setCommunityId(communityId)
                .setUserId(userId.toString())
                .build()
            )
            .build();
    }
}
