package com.ambrosia.content_service.community.repository.custom;

import java.util.List;
import java.util.UUID;

import com.ambrosia.community_service.kafka_events.CommunityEvent;
import com.ambrosia.content_service.community.model.entity.CommunityProjection;

public interface CustomCommunityProjectionRepository {
    void process(List<CommunityEvent> toUpsert, List<CommunityEvent> toDelete);
}
