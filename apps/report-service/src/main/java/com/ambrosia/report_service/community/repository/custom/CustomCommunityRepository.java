package com.ambrosia.report_service.community.repository.custom;

import com.ambrosia.community_service.kafka_events.CommunityEvent;

import java.util.List;

public interface CustomCommunityRepository {
    void process(List<CommunityEvent> toUpsert, List<CommunityEvent> toDelete);
}
