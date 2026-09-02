package com.ambrosia.comment_service.community.repository;

import java.util.List;

import com.ambrosia.community_service.kafka_events.CommunityBanEvent;

public interface CustomCommunityBanRepository {
    void batchModify(List<CommunityBanEvent> toInsert, List<CommunityBanEvent> toDelete);
}
