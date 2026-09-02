package com.ambrosia.content_service.community.repository.custom;

import java.util.List;

import com.ambrosia.community_service.kafka_events.CommunityBanEvent;

public interface CustomCommunityBanProjectionRepository {
    void batchModify(List<CommunityBanEvent> toInsert, List<CommunityBanEvent> toDelete);
}
