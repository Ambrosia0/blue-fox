package com.ambrosia.community_service.infrastructure.kafka.utils;

import java.util.UUID;

import com.ambrosia.community_service.kafka_events.CommunityBan;
import com.ambrosia.community_service.kafka_events.CommunityBanEvent;
import com.ambrosia.community_service.kafka_events.CommunityUnban;

public class CommunityBanEventFactory {
    public static CommunityBanEvent createBan(
            com.ambrosia.community_service.community.domain.entity.CommunityBan ban){
        return CommunityBanEvent.newBuilder()
            .setBan(CommunityBan.newBuilder()
                .setCommunityId(ban.getId().communityId())
                .setUserId(ban.getId().userId().toString())
                .setBeforeDate(ban.getId().beforeDate().toEpochMilli())
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
